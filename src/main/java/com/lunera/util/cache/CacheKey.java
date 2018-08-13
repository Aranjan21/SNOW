package com.lunera.util.cache;

import lombok.Data;

@Data
public class CacheKey {
	private String customerid;
	private String type;
	private String deviceId;
	private String transID;
}
