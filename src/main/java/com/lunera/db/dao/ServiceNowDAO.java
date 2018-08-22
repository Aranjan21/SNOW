package com.lunera.db.dao;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import com.lunera.dto.LuneraBuilding;
import com.lunera.dto.LuneraCustomer;
import com.lunera.dto.ServiceNowData;

public interface ServiceNowDAO {

	public Map<String, String> findContainerIdsbyLampSerialNumber(String serialNumber);

	public boolean isServiceCountAlreadyUpdated(ServiceNowData data);

	/**
	 * This method update snapshot of service now button count
	 * 
	 * @param data
	 * @return boolean
	 */
	public boolean updateServiceNowCount(ServiceNowData data);

	/**
	 * This method fetch all the customers.
	 * 
	 * @return List<LuneraCustomer>
	 */
	public List<LuneraCustomer> getAllCustomer();

	/**
	 * This method fetch all the building of given customer.
	 * 
	 * @param customerId
	 * @return List<LuneraBuilding>
	 */
	public List<LuneraBuilding> getAllBuilding(String customerId);
}
