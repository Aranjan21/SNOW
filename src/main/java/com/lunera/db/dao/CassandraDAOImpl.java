package com.lunera.db.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunera.config.CassandraManager;
import com.lunera.controller.ServiceNowController;
import com.lunera.dto.ServiceNowData;
import com.lunera.dto.ServiceNowRawModel;
import com.lunera.dto.ServiceNowSummarizeData;
import com.lunera.dto.ServiceNowSummaryModel;
import com.lunera.request.RawDataRequest;
import com.lunera.request.SummarizeDataRequest;
import com.lunera.response.ServiceNowRawData;
import com.lunera.util.TimeUtil;
import com.lunera.util.enums.ApplicationConstants;
import com.lunera.util.enums.TimePeriod;

@Service
public class CassandraDAOImpl implements CassandraDAO {

	private final static Logger logger = LogManager.getLogger(ServiceNowController.class);

	@Autowired
	private CassandraManager cassandraManager;

	@Autowired
	private TimeUtil timeUtil;

	@Override
	public void saveServiceNowData(ServiceNowData data) {
		String query = "insert into service_now (buildingid,buttonid,servicetype,timestamp,transid)" + " values('"
				+ data.getBuildingid() + "','" + data.getDeviceId() + "'," + data.getType() + ",'"
				+ data.getPublished_at() + "'," + data.getTransID() + ")";
		cassandraManager.executeSynchronously(query);
		logger.info("Service now data saved to cassandra database:" + query);
	}

	public void saveSummaryData(ServiceNowSummaryModel model, TimePeriod period) {
		String collectionName = getCollectionName(period);
		ObjectMapper mapper = new ObjectMapper();
		String doc;
		try {
			doc = mapper.writeValueAsString(model);
			String query = "insert into " + collectionName + " JSON '" + doc + "';";
			cassandraManager.executeSynchronously(query);
			logger.info("Service now data saved to cassandra database:" + query);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	public ServiceNowSummaryModel getLatestServiceNowReport(String buildingId, TimePeriod period) {
		ServiceNowSummaryModel summaryData;
		String collectionName = getCollectionName(period);
		String query = "select * from " + collectionName + " where " + "buildingId = '" + buildingId + "' "
				+ "limit 1;";
		ResultSet rs = cassandraManager.executeSynchronously(query);
		if (!rs.isExhausted()) {
			summaryData = getSummaryModel(rs.one());
		} else {
			summaryData = new ServiceNowSummaryModel();
		}
		return summaryData;
	}

	private String getCollectionName(TimePeriod period) {
		String collection = "service_now";
		switch (period) {
		case Daily:
			collection += "_summary_day";
			break;
		case Hourly:
			collection += "_summary_hour";
			break;
		default:
			break;
		}
		return collection;
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

	public ServiceNowSummaryModel getSummaryModel(Row row) {
		ServiceNowSummaryModel data = new ServiceNowSummaryModel();
		data.setBuildingId(row.getString("buildingId"));
		data.setTotalHappy(row.getInt("totalHappy"));
		data.setTotalSad(row.getInt("totalSad"));
		data.setTotalService(row.getInt("totalService"));
		data.setTimestamp(row.getTimestamp("timestamp"));
		return data;
	}

	@Override
	public List<ServiceNowRawModel> getServiceNowReport(String buildingId, TimePeriod period, DateTime from,
			DateTime to) {
		List<ServiceNowRawModel> listRawModel = new ArrayList<ServiceNowRawModel>();
		ResultSet rs = getServiceNowResultSet(buildingId, period, from, to);
		if (rs != null) {
			Row row = rs.one();
			while (row != null) {
				listRawModel.add(getRawDataModel(row));
				row = rs.one();
			}
		}
		return listRawModel;
	}

	public List<ServiceNowSummaryModel> getServiceNowReportSummary(String buildingId, TimePeriod period, DateTime from,
			DateTime to) {
		List<ServiceNowSummaryModel> listSummaryModel = new ArrayList<ServiceNowSummaryModel>();
		ResultSet rs = getServiceNowResultSet(buildingId, period, from, to);
		if (rs != null) {
			Row row = rs.one();
			while (row != null) {
				listSummaryModel.add(getSummaryModel(row));
				row = rs.one();
			}
		}
		return listSummaryModel;
	}

	private ServiceNowRawModel getRawDataModel(Row row) {
		ServiceNowRawModel data = new ServiceNowRawModel();
		data.setBuildingId(row.getString("buildingId"));
		data.setButtonId(row.getString("buttonId"));
		data.setServiceType(row.getInt("serviceType"));
		data.setTimestamp(row.getTimestamp("timestamp"));
		return data;
	}

	private ResultSet getServiceNowResultSet(String buildingId, TimePeriod period, DateTime from, DateTime to) {
		TimePeriod nextGranularityLevel = timeUtil.getNextGranularityLevel(period);
		String collectionName = getCollectionName(nextGranularityLevel);

		String query = "select * from " + collectionName + " where " + "buildingId = '" + buildingId + "' "
				+ "and timestamp >= '" + from + "' " + "and timestamp < '" + to + "';";
		logger.info("Execution query : " + query);
		ResultSet rs = cassandraManager.executeSynchronously(query);
		return rs;
	}

}
