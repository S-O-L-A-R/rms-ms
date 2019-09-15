package com.solar.ms.rms.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.solar.ms.rms.model.CreateMenuItemRequest;
import com.solar.ms.rms.model.RemoveMenuItemRequest;
import com.solar.ms.rms.model.firestore.DraftMenuItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class MenuItemService {

    @Autowired
    private Firestore firestore;

    public DocumentReference addMenuItem(CreateMenuItemRequest request) throws ExecutionException, InterruptedException {
        CollectionReference docRef = firestore.collection("restaurants").document("restaurant-1").collection("draft-menu-items");

        List<QueryDocumentSnapshot> list = getDraftMenuItemList(request.getUser().getId(), request.getMenuId(), request.getMemo(), request.getTableId());
        if(list.isEmpty()){
            DraftMenuItem draftMenuItem = new DraftMenuItem();
            draftMenuItem.setQuantity(request.getQuantity());
            draftMenuItem.setMemo(request.getMemo());
            draftMenuItem.setMenuId(request.getMenuId());
            draftMenuItem.setTableId(request.getTableId());
            draftMenuItem.setUser(request.getUser());

            ApiFuture<DocumentReference> result = docRef.add(draftMenuItem);
            return result.get();
        }else{
            QueryDocumentSnapshot queryDocumentSnapshot = list.get(0);
            DraftMenuItem draftMenuItem = queryDocumentSnapshot.toObject(DraftMenuItem.class);
            list.get(0).getReference().update("quantity", draftMenuItem.getQuantity() + 1).get();
            return queryDocumentSnapshot.getReference();
        }
    }

    public int removeMenuItem(RemoveMenuItemRequest request) throws ExecutionException, InterruptedException {
        QueryDocumentSnapshot toBeRemoved = this.getDraftMenuItem(request.getUser().getId(), request.getMenuId(), request.getMemo(), request.getTableId());
        DocumentReference toBeRemovedReference = toBeRemoved.getReference();
        DraftMenuItem draftMenuItem = toBeRemoved.toObject(DraftMenuItem.class);

        if(draftMenuItem.getQuantity() - 1 > 0){
            toBeRemovedReference.update("quantity", draftMenuItem.getQuantity() - 1).get();
        }else{
            toBeRemovedReference.delete().get();
        }
//        DraftMenuItem draftMenuItem = new DraftMenuItem();
//        draftMenuItem.setQuantity(request.getQuantity());
//        draftMenuItem.setMemo(request.getMemo());
//        draftMenuItem.setMenuId(request.getMenuId());
//        draftMenuItem.setTableId(request.getTableId());
//        draftMenuItem.setUser(request.getUser());
//
//        ApiFuture<DocumentReference> result = docRef.add(draftMenuItem);
//
        return draftMenuItem.getQuantity() - 1;
    }

    public QueryDocumentSnapshot getDraftMenuItem(String userId, String menuId, String memo, String tableId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> docRef = firestore.collection("restaurants")
                .document("restaurant-1")
                .collection("draft-menu-items")
                .whereEqualTo("user.id", userId)
                .whereEqualTo("menuId", menuId)
                .whereEqualTo("memo", memo)
                .whereEqualTo("tableId", tableId).get();

        return docRef.get().getDocuments().get(0);
    }

    public List<QueryDocumentSnapshot> getDraftMenuItemList(String userId, String menuId, String memo, String tableId) throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> docRef = firestore.collection("restaurants")
                .document("restaurant-1")
                .collection("draft-menu-items")
                .whereEqualTo("user.id", userId)
                .whereEqualTo("menuId", menuId)
                .whereEqualTo("memo", memo)
                .whereEqualTo("tableId", tableId).get();

        return docRef.get().getDocuments();
    }
}
