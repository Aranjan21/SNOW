package com.lunera.dto;

import lombok.Data;

@Data
public class ServiceNowData {
	private String coreid;
	private String id;
	private String published_at;
	private String ttl;
	private String event;
	private String type;
	private String deviceId;
	private String transID;

	private String customerid;
	private String buildingid;
	private String floorid;
	private String tenantid;
}
