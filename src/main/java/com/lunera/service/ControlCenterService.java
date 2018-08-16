package com.lunera.service;

import com.lunera.request.RawDataRequest;
import com.lunera.request.SummarizeDataRequest;
import com.lunera.response.RawDataResponse;
import com.lunera.response.SummarizeDataResponse;

/**
 * 
 * @author gautam.vijay added on 8 Aug 2018
 */
public interface ControlCenterService {

	public SummarizeDataResponse getSummaryData(SummarizeDataRequest summaryDataRequest);

	public RawDataResponse getRawData(RawDataRequest rawDataRequest);
}
