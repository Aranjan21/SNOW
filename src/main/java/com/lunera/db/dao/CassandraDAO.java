package com.lunera.db.dao;

import java.util.List;

import org.joda.time.DateTime;

import com.lunera.dto.ServiceNowData;
import com.lunera.dto.ServiceNowRawModel;
import com.lunera.dto.ServiceNowSummarizeData;
import com.lunera.dto.ServiceNowSummaryModel;
import com.lunera.request.RawDataRequest;
import com.lunera.request.SummarizeDataRequest;
import com.lunera.response.ServiceNowRawData;
import com.lunera.util.enums.TimePeriod;

public interface CassandraDAO {
	public void saveServiceNowData(ServiceNowData data);

	public List<ServiceNowSummarizeData> getHourlyServiceNowSummaryData(SummarizeDataRequest request);
	
	public List<ServiceNowSummarizeData> getDailyServiceNowSummaryData(SummarizeDataRequest request);

	public List<ServiceNowRawData> getServiceNowRawData(RawDataRequest request);
	
	public ServiceNowSummaryModel getLatestServiceNowReport(String buildingId, TimePeriod period);
	
	public List<ServiceNowRawModel> getServiceNowReport(String buildingId, TimePeriod period,DateTime from, DateTime to);
	
	public void saveSummaryData(ServiceNowSummaryModel model, TimePeriod period);
	
	public List<ServiceNowSummaryModel> getServiceNowReportSummary(String buildingId, TimePeriod period, DateTime from,
			DateTime to);
}
