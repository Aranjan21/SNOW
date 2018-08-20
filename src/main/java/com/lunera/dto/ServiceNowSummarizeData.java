package com.lunera.dto;

import lombok.Data;

@Data
public class ServiceNowSummarizeData {
	private String startDate;
	private String endDate;
	private String buildingId;
	private int totalHappy;
	private int totalSad;
	private int totalService;
	private String responseTime;
}
