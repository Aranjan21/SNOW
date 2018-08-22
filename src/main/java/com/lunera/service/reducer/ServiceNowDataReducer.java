package com.lunera.service.reducer;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lunera.db.dao.CassandraDAO;
import com.lunera.dto.LuneraBuilding;
import com.lunera.dto.ServiceNowRawModel;
import com.lunera.dto.ServiceNowSummaryModel;
import com.lunera.util.TimeUtil;
import com.lunera.util.enums.TimePeriod;

@Component
public class ServiceNowDataReducer {

	private final static Logger logger = LogManager.getLogger(ServiceNowDataReducer.class);

	@Autowired
	private CassandraDAO cassandraDAO;

	@Autowired
	private TimeUtil timeUtil;

	/**
	 * This method trigger hourly and daily reducer for service now
	 * 
	 * @param building
	 */
	public void reduceBuilding(LuneraBuilding building) {
		reduceBuildingByPeriod(building, TimePeriod.Hourly);
		reduceBuildingByPeriod(building, TimePeriod.Daily);
	}

	/**
	 * This method start service now reducer for building with period type
	 * 
	 * @param building
	 * @param period
	 */
	private void reduceBuildingByPeriod(LuneraBuilding building, TimePeriod period) {
		ServiceNowSummaryModel report = cassandraDAO.getLatestServiceNowReport(building.getId(), period);

		DateTime startTimestamp = timeUtil.getStartTimestamp(report.getTimestamp(), period);
		DateTime now = new DateTime(DateTimeZone.UTC);

		switch (period) {
		case Daily:
			startTimestamp = startTimestamp.withHourOfDay(7).hourOfDay().roundFloorCopy();
			break;
		case Monthly:
			startTimestamp = startTimestamp.withDayOfMonth(1).withHourOfDay(7).hourOfDay().roundFloorCopy();
			break;
		case Hourly:
		default:
			startTimestamp = startTimestamp.hourOfDay().roundFloorCopy();
			break;
		}

		DateTime iteratorTimeStamp = startTimestamp;
		while (iteratorTimeStamp.isBefore(timeUtil.minusPeriod(now, period))) {
			DateTime periodStart = iteratorTimeStamp;
			DateTime periodEnd = timeUtil.plusPeriod(iteratorTimeStamp, period);
			ServiceNowSummaryModel model = new ServiceNowSummaryModel();
			switch (period) {
			case Hourly:
				model = buildServiceNowSummaryModelHourly(building.getId(), period, periodStart, periodEnd);
				break;
			case Daily:
				model = buildServiceNowSummaryModelDaily(building.getId(), period, periodStart, periodEnd);
				break;
			default:
				logger.info("This is default case : Nothing to execute");

			}
			model.setBuildingId(building.getId());
			model.setTimestamp(periodEnd.toDate());
			// model.setResponseTime("0");
			cassandraDAO.saveSummaryData(model, period);

			iteratorTimeStamp = periodEnd;
		}
	}

	/**
	 * This method return hourly summary data from raw service now table.
	 * 
	 * @param buildingId
	 * @param period
	 * @param from
	 * @param to
	 * @return ServiceNowSummaryModel
	 */
	private ServiceNowSummaryModel buildServiceNowSummaryModelHourly(String buildingId, TimePeriod period,
			DateTime from, DateTime to) {
		int totalHappy = 0;
		int totalSad = 0;
		int totalService = 0;
		List<ServiceNowRawModel> listRawData = cassandraDAO.getServiceNowReport(buildingId, period, from, to);
		for (ServiceNowRawModel rawData : listRawData) {
			if (rawData.getServiceType() == 0) {
				totalHappy++;
			} else if (rawData.getServiceType() == 1) {
				totalSad++;
			} else {
				totalService++;
			}
		}
		ServiceNowSummaryModel model = new ServiceNowSummaryModel();
		model.setTotalHappy(totalHappy);
		model.setTotalSad(totalSad);
		model.setTotalService(totalService);
		return model;
	}

	/**
	 * This method return summary data for daily table from hourly table
	 * 
	 * @param buildingId
	 * @param period
	 * @param from
	 * @param to
	 * @return ServiceNowSummaryModel
	 */
	private ServiceNowSummaryModel buildServiceNowSummaryModelDaily(String buildingId, TimePeriod period, DateTime from,
			DateTime to) {
		int totalHappy = 0;
		int totalSad = 0;
		int totalService = 0;
		List<ServiceNowSummaryModel> list = cassandraDAO.getServiceNowReportSummary(buildingId, period, from, to);
		for (ServiceNowSummaryModel mode : list) {
			totalHappy = totalHappy + mode.getTotalHappy();
			totalSad = totalSad + mode.getTotalSad();
			totalService = totalService + mode.getTotalService();
		}
		ServiceNowSummaryModel model = new ServiceNowSummaryModel();
		model.setTotalHappy(totalHappy);
		model.setTotalSad(totalSad);
		model.setTotalService(totalService);
		return model;
	}
}
