package com.lunera.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ServiceNowSummaryModel {
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private Date timestamp;
	private String buildingId;
	private int totalHappy;
	private int totalSad;
	private int totalService;
//	private String responseTime;
}
