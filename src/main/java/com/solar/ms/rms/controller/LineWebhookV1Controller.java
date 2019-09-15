package com.solar.ms.rms.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.solar.ms.rms.model.line.LineMessage;
import com.solar.ms.rms.model.line.MessageRequest;
import com.solar.ms.rms.service.LineMessageService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/v1/webhook")
@RestController
public class LineWebhookV1Controller {

	@Autowired
	LineMessageService lineMessageService;

	@PostMapping
	public ResponseEntity<String> handleHookEvent(@RequestBody MessageRequest messageRequest) {
		if (messageRequest.getType().equals("message")) {
			LineMessage lineMessage = new LineMessage("text", "Hello, World", false);
			lineMessageService.sendPushMessage(messageRequest.getSource().getUserId(), Collections.singletonList(lineMessage));
			return ResponseEntity.ok("success");
		}
		return ResponseEntity.ok("success");
	}
}
