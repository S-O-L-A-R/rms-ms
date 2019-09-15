package com.solar.ms.rms.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.solar.ms.rms.model.line.LineMessage;
import com.solar.ms.rms.model.payment.ConfirmPaymentRequest;
import com.solar.ms.rms.model.payment.ReservePaymentRequest;
import com.solar.ms.rms.service.LineMessageService;
import com.solar.ms.rms.service.OrderService;
import com.solar.ms.rms.service.PaymentService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.solar.ms.rms.model.SummaryRequest;
import com.solar.ms.rms.model.firestore.DraftMenuItem;
import com.solar.ms.rms.model.order.Menu;
import com.solar.ms.rms.model.order.MenuItem;
import com.solar.ms.rms.model.order.Order;
import com.solar.ms.rms.model.order.OrderItem;
import com.solar.ms.rms.model.order.OrderTotal;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/v1/summary")
@RestController
public class SummaryV1Controller {
	private static final String DRAFT_ORDER_ITEMS_COLLECTION = "draft-menu-items";

	@Autowired
	private Firestore firestore;

	@Autowired
	private HttpHeaders httpHeaders;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private LineMessageService lineMessageService;

	@Value("${service.rms-ms.payment-callback.url}")
	private String paymentCallbackUrl;

	@PostMapping
	public ResponseEntity<Order> generateSummary(@RequestBody SummaryRequest summaryRequest)
			throws ExecutionException, InterruptedException {
		ApiFuture<QuerySnapshot> querySnapshotApiFuture1 = firestore.collection("restaurants")
				.document("restaurant-1")
				.collection("menus")
				.get();

		Map<String, Menu> menuIds = querySnapshotApiFuture1.get()
				.getDocuments()
				.stream()
				.collect(Collectors.toMap(DocumentSnapshot::getId,
						queryDocumentSnapshot -> queryDocumentSnapshot.toObject(Menu.class)));

		ApiFuture<QuerySnapshot> draftMenuItemsFuture = firestore.collection("restaurants")
				.document("restaurant-1")
				.collection(DRAFT_ORDER_ITEMS_COLLECTION)
				.whereEqualTo("user.id", summaryRequest.getUserId())
				.whereEqualTo("tableId", summaryRequest.getTableId())
				.get();

		ApiFuture<QuerySnapshot> ordersFuture = firestore.collection("restaurants")
				.document("restaurant-1")
				.collection("orders")
				.whereEqualTo("userId", summaryRequest.getUserId())
				.whereEqualTo("tableId", summaryRequest.getTableId())
				.whereEqualTo("state", "PENDING")
				.get();

		List<QueryDocumentSnapshot> documents = ordersFuture.get().getDocuments();
		for (QueryDocumentSnapshot document : documents) {
			document.getReference().delete();
		}

		List<DraftMenuItem> draftOrderItems = draftMenuItemsFuture.get()
				.getDocuments()
				.stream()
				.map(queryDocumentSnapshot -> queryDocumentSnapshot.toObject(DraftMenuItem.class))
				.collect(Collectors.toList());

		Map<String, OrderItem> orderItems = draftOrderItems.stream()
				.collect(Collectors.toMap(draftMenuItem -> RandomStringUtils.randomAlphanumeric(10), draftMenuItem -> {
					OrderItem orderItem = new OrderItem();
					orderItem.setMemo(draftMenuItem.getMemo());
					orderItem.setQuantity(draftMenuItem.getQuantity());
					MenuItem menuItem = new MenuItem();
					String menuId = draftMenuItem.getMenuId();
					menuItem.setId(menuId);
					Menu menu = menuIds.get(menuId);
					menuItem.setName(menu.getName());
					orderItem.setMenu(menuItem);
					orderItem.setUnitPrice(menu.getPrice());
					orderItem.setAmount(orderItem.getUnitPrice() * orderItem.getQuantity());
					return orderItem;
				}));

		Order order = new Order();
		order.setCreatedDate(Timestamp.now());
		order.setState("PENDING");
		order.setTableId(summaryRequest.getTableId());
		order.setUserId(summaryRequest.getUserId());
		order.setItems(orderItems);

		Integer totalQuantity = orderItems.values().stream().map(OrderItem::getQuantity).reduce(0, Integer::sum);
		Double totalAmount = orderItems.values().stream().map(OrderItem::getAmount).reduce(0d, Double::sum);

		OrderTotal orderTotal = new OrderTotal();
		orderTotal.setAmount(totalAmount);
		orderTotal.setQuantity(totalQuantity);
		order.setTotal(orderTotal);

		ApiFuture<DocumentReference> addedOrder = firestore.collection("restaurants")
				.document("restaurant-1")
				.collection("orders")
				.add(order);
		order.setId(addedOrder.get().getId());

		return ResponseEntity.ok(order);
	}

	@PostMapping(value = "/pay")
	public ResponseEntity<?> payBill(@RequestBody SummaryRequest summaryRequest) throws ExecutionException, InterruptedException {
		QueryDocumentSnapshot orderQueryDocument = orderService.getOrder(summaryRequest.getUserId(), summaryRequest.getTableId(), "PENDING");
		Order order = orderQueryDocument.toObject(Order.class);

		ReservePaymentRequest reservePaymentRequest = new ReservePaymentRequest();
		reservePaymentRequest.setProductName("ค่าอาหาร");
		reservePaymentRequest.setOrderId(orderQueryDocument.getId());
		reservePaymentRequest.setAmount(order.getTotal().getAmount());
		reservePaymentRequest.setConfirmUrl(paymentCallbackUrl);

		return paymentService.reservePayment(reservePaymentRequest);
	}

	@GetMapping(value = "/pay/callback")
	public ResponseEntity<?> paymentCallback(@RequestParam String transactionId, @RequestParam String orderId) throws ExecutionException, InterruptedException {
		log.info("HEADERS: {}, transactionId: {}, orderId: {}", httpHeaders, transactionId, orderId);

		DocumentSnapshot documentSnapshot = orderService.getOrderByOrderId(orderId);
		Order order = documentSnapshot.toObject(Order.class);

		ConfirmPaymentRequest confirmRequest = new ConfirmPaymentRequest();
		confirmRequest.setTransactionId(transactionId);
		confirmRequest.setAmount(order.getTotal().getAmount());

		ResponseEntity<?> confirmationResponse =  paymentService.confirmPayment(confirmRequest);

		log.info("RESPONSE FROM LINE: {}", confirmationResponse);

		documentSnapshot.getReference().update("state", "PAID").get();

		// PUSH NOTIFICATION TO USER
		lineMessageService.sendPushMessage(order.getUserId(), Collections.singletonList(
				new LineMessage("message", "ชำระเงินเสร็จสิ้น", false)
		));

		return new ResponseEntity<>(HttpStatus.OK);
	}

}