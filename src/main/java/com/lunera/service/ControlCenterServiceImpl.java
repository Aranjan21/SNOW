package com.lunera.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lunera.db.dao.CassandraDAO;
import com.lunera.dto.ServiceNowSummarizeData;
import com.lunera.request.RawDataRequest;
import com.lunera.request.SummarizeDataRequest;
import com.lunera.response.RawDataResponse;
import com.lunera.response.ServiceNowRawData;
import com.lunera.response.SummarizeDataResponse;

/**
 * 
 * @author gautam.vijay added on 8 Aug 2018
 */
@Service
public class ControlCenterServiceImpl implements ControlCenterService {

	@Autowired
	private CassandraDAO cassandraDAO;

	@Override
	public SummarizeDataResponse getSummaryData(SummarizeDataRequest summaryDataRequest) {
		SummarizeDataResponse response = new SummarizeDataResponse();
		List<ServiceNowSummarizeData> summaryDataList = null;
		switch (summaryDataRequest.getPeriod()) {
		case daily:
			summaryDataList = cassandraDAO.getDailyServiceNowSummaryData(summaryDataRequest);
			break;
		case hour:
			summaryDataList = cassandraDAO.getHourlyServiceNowSummaryData(summaryDataRequest);
			break;
		default:

		}
		response.setSummaryData(summaryDataList);
		return response;
	}

	@Override
	public RawDataResponse getRawData(RawDataRequest rawDataRequest) {
		RawDataResponse rawDataResponse = new RawDataResponse();
		List<ServiceNowRawData> rawDataList = new ArrayList<ServiceNowRawData>();
		rawDataList = cassandraDAO.getServiceNowRawData(rawDataRequest);
		rawDataResponse.setRawData(rawDataList);
		return rawDataResponse;
	}

}
