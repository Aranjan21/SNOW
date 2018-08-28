package com.lunera.response;

import lombok.Data;

@Data
public class ServiceNowRawData {
	private String buildingId;
	private String buttonId;
	private String publishedDate;
	private int serviceType;
	private String servicedDate;// related to third service
}
