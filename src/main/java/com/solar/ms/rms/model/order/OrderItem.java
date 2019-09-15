package com.solar.ms.rms.model.order;

import lombok.Data;

@Data
public class OrderItem {
	private MenuItem menu;
	private String memo;
	private Double amount;
	private Double unitPrice;
	private Integer quantity;
}
