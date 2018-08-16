package com.lunera.db.dao;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.lunera.config.CassandraManager;
import com.lunera.dto.ServiceNowData;
import com.lunera.dto.ServiceNowSummarizeData;
import com.lunera.request.RawDataRequest;
import com.lunera.request.SummarizeDataRequest;
import com.lunera.response.ServiceNowRawData;

@Service
public class CassandraDAOImpl implements CassandraDAO {

	@Autowired
	private CassandraManager cassandraManager;

	@Override
	public void saveServiceNowData(ServiceNowData data) {
		String query = "insert into service_now (buildingid,buttonid,servicetype,timestamp,transid)" + " values('"
				+ data.getBuildingid() + "'," + data.getDeviceId() + "," + data.getType() + ",'"
				+ data.getPublished_at() + "'," + data.getTransID() + ")";
		cassandraManager.executeSynchronously(query);
	}

	public List<ServiceNowSummarizeData> getHourlyServiceNowSummaryData(SummarizeDataRequest request) {
		List<ServiceNowSummarizeData> responseList = new ArrayList<ServiceNowSummarizeData>();
		String query = "select * from service_now_summary_hour where " + "buildingId = '" + request.getBuildingId()
				+ "' " + "and timestamp > '" + request.getFrom() + "' " + "and timestamp <= '" + request.getTo() + "';";
		ResultSet rs = cassandraManager.executeSynchronously(query);
		if (rs != null) {
			Row row = rs.one();
			while (row != null) {
				responseList.add(getSummaryData(row));
				row = rs.one();
			}
		}
		return responseList;
	}

	public List<ServiceNowSummarizeData> getDailyServiceNowSummaryData(SummarizeDataRequest request) {
		List<ServiceNowSummarizeData> responseList = new ArrayList<ServiceNowSummarizeData>();
		String query = "select * from service_now_summary_day where " + "buildingId = '" + request.getBuildingId()
				+ "' " + "and timestamp > '" + request.getFrom() + "' " + "and timestamp <= '" + request.getTo() + "';";
		ResultSet rs = cassandraManager.executeSynchronously(query);
		if (rs != null) {
			Row row = rs.one();
			while (row != null) {
				responseList.add(getSummaryData(row));
				row = rs.one();
			}
		}
		return responseList;
	}

	public ServiceNowSummarizeData getSummaryData(Row row) {
		ServiceNowSummarizeData data = new ServiceNowSummarizeData();
		data.setBuildingId(row.getString("buildingId"));
		data.setTotalHappy(row.getInt("totalHappy"));
		data.setTotalSad(row.getInt("totalSad"));
		data.setTotalService(row.getInt("totalService"));
		data.setTimestamp(row.getString("timestamp"));
		return data;
	}

	public List<ServiceNowRawData> getHourlyServiceNowRawData(RawDataRequest request) {
		String query = "select * from service_now where " + "buildingId = '" + request.getBuildingId() + "' "
				+ "and timestamp >= '" + request.getFrom() + "' " + "and timestamp < '" + request.getTo() + "';";
		return null;

	}

	public List<ServiceNowRawData> getDailyServiceNowRawData(RawDataRequest request) {
		String query = "select * from service_now where " + "buildingId = '" + request.getBuildingId() + "' "
				+ "and timestamp >= '" + request.getFrom() + "' " + "and timestamp < '" + request.getTo() + "';";
		return null;

	}

}
