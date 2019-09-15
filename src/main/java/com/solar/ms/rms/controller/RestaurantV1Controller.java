package com.solar.ms.rms.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.storage.Blob;
import com.google.firebase.cloud.StorageClient;
import com.solar.ms.rms.model.MenuRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/v1/restaurant")
@RestController
public class RestaurantV1Controller {

	@Autowired
	private Firestore firestore;

	@Autowired
	private StorageClient storageClient;

	@PostMapping("/add-menu")
	public ResponseEntity<String> addMenu(@ModelAttribute MenuRequest menuRequest)
			throws IOException, ExecutionException, InterruptedException {
		String menuPhotoName = menuRequest.getFile().getOriginalFilename();
		InputStream menuPhotoStream = menuRequest.getFile().getInputStream();

		log.info("{}", menuPhotoName);
		Blob blob = storageClient.bucket().create(Objects.requireNonNull(menuPhotoName), menuPhotoStream);

		String blobMediaLink = blob.getMediaLink();

		Map<String, Object> body = new HashMap<>();
		body.put("name", menuRequest.getName());
		body.put("description", menuRequest.getDescription());
		body.put("price", menuRequest.getPrice());
		body.put("thumbnailUrl", blobMediaLink);

		ApiFuture<DocumentReference> docMenuFuture = firestore.collection("restaurants")
				.document("restaurant-1")
				.collection("menus")
				.add(body);

		return ResponseEntity.ok(docMenuFuture.get().getId());
	}

}
