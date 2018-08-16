package com.lunera.dto;

import lombok.Data;

@Data
public class ServiceNowSummarizeData {
	private String buildingId;
	private String timestamp;
	private int totalHappy;
	private int totalSad;
	private int totalService;
	private String responseTime;
}
