package com.solar.ms.rms.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.solar.ms.rms.model.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class OrderService {

    @Autowired
    private Firestore firestore;

    public QueryDocumentSnapshot getOrder(String userId, String tableId, String status) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> order = firestore.collection("restaurants")
                .document("restaurant-1")
                .collection("orders")
                .whereEqualTo("userId", userId)
                .whereEqualTo("tableId", tableId)
                .whereEqualTo("status", status).get();

        return order.get().getDocuments().get(0);
    }

    public DocumentSnapshot getOrderByOrderId(String orderId) throws ExecutionException, InterruptedException {
        DocumentReference order = firestore.collection("restaurants")
                .document("restaurant-1")
                .collection("orders")
                .document(orderId);

        return order.get().get();
    }
}
