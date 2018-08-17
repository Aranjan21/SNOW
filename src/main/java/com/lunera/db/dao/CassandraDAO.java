package com.lunera.db.dao;

import java.util.List;

import com.lunera.dto.ServiceNowData;
import com.lunera.dto.ServiceNowSummarizeData;
import com.lunera.request.RawDataRequest;
import com.lunera.request.SummarizeDataRequest;
import com.lunera.response.ServiceNowRawData;

public interface CassandraDAO {
	public void saveServiceNowData(ServiceNowData data);

	public List<ServiceNowSummarizeData> getHourlyServiceNowSummaryData(SummarizeDataRequest request);
	
	public List<ServiceNowSummarizeData> getDailyServiceNowSummaryData(SummarizeDataRequest request);

	public List<ServiceNowRawData> getServiceNowRawData(RawDataRequest request);
}
