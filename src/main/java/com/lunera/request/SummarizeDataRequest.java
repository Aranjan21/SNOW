package com.lunera.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lunera.util.enums.TimePeriod;

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
	@JsonDeserialize
	private TimePeriod period;
}
