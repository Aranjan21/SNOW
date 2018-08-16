package com.lunera.response;

import lombok.Data;

@Data
public class ServiceNowRawData {
	private String uuid;
	private String buttonId;
	private String publishedDate;
	private String servicedDate;
}
