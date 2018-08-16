package com.lunera.request;

import org.joda.time.DateTime;

import com.lunera.util.enums.TimePeriod;

import lombok.Data;

/**
 * 
 * @author gautam.vijay added on 8 Aug 2018
 */
@Data
public class RawDataRequest {
	private String buildingId;
	private DateTime from;
	private DateTime to;
	private TimePeriod period;
}
