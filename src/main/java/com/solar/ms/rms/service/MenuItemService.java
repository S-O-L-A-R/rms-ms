package com.solar.ms.rms.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.solar.ms.rms.model.CreateMenuItemRequest;
import com.solar.ms.rms.model.firestore.DraftMenuItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class MenuItemService {

    @Autowired
    private Firestore firestore;

    public DocumentReference insertMenuItem(CreateMenuItemRequest request) throws ExecutionException, InterruptedException {
        CollectionReference docRef = firestore.collection("restaurants").document("restaurant-1").collection("draft-menu-items");

        DraftMenuItem draftMenuItem = new DraftMenuItem();
        draftMenuItem.setQuantity(request.getQuantity());
        draftMenuItem.setMemo(request.getMemo());
        draftMenuItem.setMenuId(request.getMenuId());
        draftMenuItem.setTableId(request.getTableId());
        draftMenuItem.setUser(request.getUser());

        ApiFuture<DocumentReference> result = docRef.add(draftMenuItem);

        return result.get();
    }
}
