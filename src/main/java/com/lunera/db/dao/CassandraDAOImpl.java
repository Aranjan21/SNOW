package com.lunera.db.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.lunera.config.CassandraManager;
import com.lunera.controller.ServiceNowController;
import com.lunera.dto.ServiceNowData;
import com.lunera.dto.ServiceNowSummarizeData;
import com.lunera.request.RawDataRequest;
import com.lunera.request.SummarizeDataRequest;
import com.lunera.response.ServiceNowRawData;
import com.lunera.util.enums.ApplicationConstants;
import com.lunera.util.enums.TimePeriod;

@Service
public class CassandraDAOImpl implements CassandraDAO {

	private final static Logger logger = LogManager.getLogger(ServiceNowController.class);

	@Autowired
	private CassandraManager cassandraManager;

	@Override
	public void saveServiceNowData(ServiceNowData data) {
		String query = "insert into service_now (buildingid,buttonid,servicetype,timestamp,transid)" + " values('"
				+ data.getBuildingid() + "','" + data.getDeviceId() + "'," + data.getType() + ",'"
				+ data.getPublished_at() + "'," + data.getTransID() + ")";
		cassandraManager.executeSynchronously(query);
		logger.info("Service now data saved to cassandra database:" + query);
	}

	public List<ServiceNowSummarizeData> getHourlyServiceNowSummaryData(SummarizeDataRequest request) {
		List<ServiceNowSummarizeData> responseList = new ArrayList<ServiceNowSummarizeData>();
		String query = "select * from service_now_summary_hour where " + "buildingId = '" + request.getBuildingId()
				+ "' " + "and timestamp > '" + request.getFrom() + "' " + "and timestamp <= '" + request.getTo() + "';";
		logger.info("Fetch Hourly Service Summary Data query : " + query);
		ResultSet rs = cassandraManager.executeSynchronously(query);
		if (rs != null) {
			Row row = rs.one();
			while (row != null) {
				responseList.add(getSummaryData(row, request.getPeriod()));
				row = rs.one();
			}
		}
		logger.info("Fetch Hourly Service Summary Data response : " + responseList);
		return responseList;
	}

	public List<ServiceNowSummarizeData> getDailyServiceNowSummaryData(SummarizeDataRequest request) {
		List<ServiceNowSummarizeData> responseList = new ArrayList<ServiceNowSummarizeData>();
		String query = "select * from service_now_summary_day where " + "buildingId = '" + request.getBuildingId()
				+ "' " + "and timestamp > '" + request.getFrom() + "' " + "and timestamp <= '" + request.getTo() + "';";
		logger.info("Fetch Daily Service Summary Data query : " + query);
		ResultSet rs = cassandraManager.executeSynchronously(query);
		if (rs != null) {
			Row row = rs.one();
			while (row != null) {
				responseList.add(getSummaryData(row, request.getPeriod()));
				row = rs.one();
			}
		}
		logger.info("Fetch Daily Service Summary Data response : " + responseList);
		return responseList;
	}

	public List<ServiceNowRawData> getServiceNowRawData(RawDataRequest request) {
		String query = "select * from service_now where " + "buildingId = '" + request.getBuildingId() + "' "
				+ "and timestamp >= '" + request.getFrom() + "' " + "and timestamp <= '" + request.getTo() + "';";
		List<ServiceNowRawData> rawDataList = new ArrayList<ServiceNowRawData>();
		logger.info("Fetch Raw data query : " + query);
		ResultSet rs = cassandraManager.executeSynchronously(query);
		if (rs != null) {
			Row row = rs.one();
			while (row != null) {
				rawDataList.add(getRawData(row));
				row = rs.one();
			}
		}
		logger.info("Fetch Raw data response:" + rawDataList);
		return rawDataList;
	}

	public ServiceNowRawData getRawData(Row row) {
		ServiceNowRawData data = new ServiceNowRawData();
		data.setBuildingId(row.getString("buildingId"));
		data.setButtonId(row.getString("buttonId"));
		data.setServiceType(row.getInt("serviceType"));
		Date date = row.getTimestamp("timestamp");
		data.setPublishedDate(ApplicationConstants.df.format(date));
		return data;
	}

	public ServiceNowSummarizeData getSummaryData(Row row, TimePeriod period) {
		ServiceNowSummarizeData data = new ServiceNowSummarizeData();
		data.setBuildingId(row.getString("buildingId"));
		data.setTotalHappy(row.getInt("totalHappy"));
		data.setTotalSad(row.getInt("totalSad"));
		data.setTotalService(row.getInt("totalService"));
		Date enddate = row.getTimestamp("timestamp");
		data.setEndDate(ApplicationConstants.df.format(enddate));
		Calendar cal = Calendar.getInstance();
		// remove next line if you're always using the current time.
		cal.setTime(enddate);
		switch (period) {
		case Hourly:
			cal.add(Calendar.HOUR, -1);
			break;
		case Daily:
			cal.add(Calendar.DATE, -1);
			break;
		default:
			cal.add(Calendar.HOUR, -1);
		}
		Date startDate = cal.getTime();
		data.setStartDate(ApplicationConstants.df.format(startDate));
		return data;
	}
}
