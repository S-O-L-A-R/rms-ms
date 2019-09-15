package com.solar.ms.rms.model;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class MenuRequest {
	private String name;
	private String description;
	private Double price;
	private MultipartFile file;
}
