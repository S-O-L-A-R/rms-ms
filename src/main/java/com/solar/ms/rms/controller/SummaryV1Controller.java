package com.solar.ms.rms.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}