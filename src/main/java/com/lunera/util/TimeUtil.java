package com.lunera.util;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Component;

import com.lunera.util.enums.TimePeriod;

@Component
public class TimeUtil {

	public DateTime plusPeriod(DateTime periodFrom, TimePeriod period) {
		switch (period) {
		case Daily:
			return periodFrom.plusDays(1);
		case Weekly:
			return periodFrom.plusWeeks(1);
		case Monthly:
			return periodFrom.plusMonths(1);
		case Hourly:
		default:
			return periodFrom.plusHours(1);
		}
	}

	public DateTime minusPeriod(DateTime periodFrom, TimePeriod period) {
		switch (period) {
		case Daily:
			return periodFrom.minusDays(1);
		case Weekly:
			return periodFrom.minusWeeks(1);
		case Monthly:
			return periodFrom.minusMonths(1);
		case Hourly:
		default:
			return periodFrom.minusHours(1);
		}
	}

	public TimePeriod getNextGranularityLevel(TimePeriod period) {
		TimePeriod nextGranularityLevel;

		switch (period) {
		case Monthly:
		case Weekly:
			nextGranularityLevel = TimePeriod.Daily;
			break;
		case Daily:
			nextGranularityLevel = TimePeriod.Hourly;
			break;
		case Hourly:
		default:
			nextGranularityLevel = TimePeriod.Default;
			break;
		}

		return nextGranularityLevel;
	}

	public DateTime getMinimumStartTimestamp(TimePeriod period) {
		int deltaSeconds;

		switch (period) {
		case Monthly:
			deltaSeconds = 31536000;
			break;
		case Weekly:
		case Daily:
			deltaSeconds = 2764800;
			break;
		case Hourly:
			deltaSeconds = 90000;
			break;
		case Default:
		default:
			deltaSeconds = 1296000;
			break;
		}

		return (new DateTime(DateTimeZone.UTC).minusSeconds(deltaSeconds));
	}

	public DateTime getStartTimestamp(Date latestReportTimestamp, TimePeriod period) {
		DateTime startTimestamp;
		DateTime latest;

		if (latestReportTimestamp == null) {
			latest = new DateTime(0);
		} else {
			latest = new DateTime(latestReportTimestamp);
		}

		DateTime minTimestamp = getMinimumStartTimestamp(getNextGranularityLevel(period));
		if (latest.isBefore(minTimestamp)) {
			startTimestamp = minTimestamp;
		} else {
			startTimestamp = latest;
		}

		return startTimestamp;
	}

	public DateTime getStartTimestamp(DateTime latestReportTimestamp, TimePeriod period) {
		DateTime startTimestamp;

		if (latestReportTimestamp == null) {
			latestReportTimestamp = new DateTime(0);
		}

		DateTime minTimestamp = getMinimumStartTimestamp(getNextGranularityLevel(period));
		if (latestReportTimestamp.isBefore(minTimestamp)) {
			startTimestamp = minTimestamp;
		} else {
			startTimestamp = latestReportTimestamp;
		}

		return startTimestamp;
	}
}
