package com.solar.ms.rms.model.order;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.cloud.Timestamp;

import lombok.Data;

@Data
public class Order {
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String id;
	private Timestamp createdDate;
	private Timestamp paidTime;
	private String tableId;
	private String userId;
	private String state;
	private Map<String, OrderItem> items;
	private OrderTotal total;
}
