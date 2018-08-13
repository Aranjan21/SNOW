package com.lunera.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 
 * @author gautam.vijay added on 8 Aug 2018
 */
@Data
public class RawDataResponse {
	private List<RawData> rawData = new ArrayList<RawData>();
}
