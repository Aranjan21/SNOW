package com.lunera.request;

import lombok.Data;

/**
 * 
 * @author gautam.vijay added on 8 Aug 2018
 */
@Data
public class SummarizeDataRequest {
	private String buildingId;
	private String from;
	private String to;
	private String period;
}
