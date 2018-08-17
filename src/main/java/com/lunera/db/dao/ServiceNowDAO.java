package com.lunera.db.dao;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import com.lunera.dto.ServiceNowData;

public interface ServiceNowDAO {

	public Map<String, String> findContainerIdsbyLampSerialNumber(String serialNumber);

	public boolean isServiceCountAlreadyUpdated(ServiceNowData data);

	public boolean updateServiceNowCount(ServiceNowData data);

	public void closeResources(ResultSet rs, Statement stmt);
}
