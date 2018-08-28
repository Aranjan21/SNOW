package com.lunera.db.dao;

import java.util.List;
import java.util.Map;

import com.lunera.dto.LuneraBuilding;
import com.lunera.dto.LuneraCustomer;
import com.lunera.dto.LuneraFloors;
import com.lunera.dto.LuneraTenants;
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

	/**
	 * This method reset pressed count to zero in service now button table.
	 * 
	 * @param tenantId
	 * @return boolean
	 */
	public boolean resetCount(String tenantId);

	/**
	 * This method return all the floors corresponding to the building
	 * 
	 * @param buildingId
	 * @return List<LuneraFloors>
	 */
	public List<LuneraFloors> getAllFloors(String buildingId);

	/**
	 * This method return all the tenants for corresponding floor.
	 * 
	 * @param floorId
	 * @return List<LuneraTenants>
	 */
	public List<LuneraTenants> getAllTenants(String floorId);
}
