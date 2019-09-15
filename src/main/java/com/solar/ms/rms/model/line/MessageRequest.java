package com.solar.ms.rms.model.line;

import lombok.Data;

@Data
public class MessageRequest {
	private String replyToken;
	private String type;
	private Long timestamp;
	private MessageSource source;
	private MessageBeacon beacon;
}
