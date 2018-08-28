package com.lunera.db.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lunera.config.RdsConnection;
import com.lunera.dto.LuneraBuilding;
import com.lunera.dto.LuneraCustomer;
import com.lunera.dto.LuneraFloors;
import com.lunera.dto.LuneraTenants;
import com.lunera.dto.ServiceNowData;

@Service
public class ServiceNowDAOImpl implements ServiceNowDAO {

	private final static Logger logger = LogManager.getLogger(ServiceNowDAOImpl.class);

	@Autowired
	private RdsConnection dbConnection;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.lunera.db.dao.ServiceNowDAO#findContainerIdsbyLampSerialNumber(java.lang.
	 * String)
	 */
	@Override
	public Map<String, String> findContainerIdsbyLampSerialNumber(String serialNumber) {
		Map<String, String> values = new HashMap<>();
		Statement stmt = null;
		ResultSet info = null;

		try {
			stmt = dbConnection.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			String sql = "select tenants.id as tenantid, floors.id as floorid, "
					+ "buildings.id as buildingid, customers.id as customerid "
					+ "from lamps, fixtures, tenants, floors, buildings, customers " + "where lamps.serialnumber = '"
					+ serialNumber + "' " + "and lamps.fixture_id = fixtures.id " + "and fixtures.visibility = 'true' "
					+ "and fixtures.tenant_id = tenants.id " + "and tenants.floor_id = floors.id "
					+ "and floors.building_id = buildings.id " + "and buildings.customer_id = customers.id;";
			logger.info("Executing query " + sql);
			info = stmt.executeQuery(sql);

			while (info.next()) {
				values.put("tenantid", info.getString("tenantid"));
				values.put("floorid", info.getString("floorid"));
				values.put("buildingid", info.getString("buildingid"));
				values.put("customerid", info.getString("customerid"));
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			closeResources(info, stmt);
		}

		return values;
	}

	public void closeResources(ResultSet rs, Statement stmt) {
		try {
			if (rs != null)
				rs.close();

			if (stmt != null)
				stmt.close();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.lunera.db.dao.ServiceNowDAO#isServiceCountAlreadyUpdated(com.lunera.dto.
	 * ServiceNowData)
	 */
	@Override
	public boolean isServiceCountAlreadyUpdated(ServiceNowData data) {
		Statement stmt = null;
		ResultSet resultSet = null;
		int deviceId = Integer.parseInt(data.getDeviceId(), 16);
		try {
			stmt = dbConnection.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			String sql = "select COUNT(*) from ServiceNowButtons where Tenant_Id ='" + data.getTenantid() + "' and "
					+ "Type ='" + data.getType() + "' and ButtonId ='" + deviceId + "' and TransId ='"
					+ data.getTransID() + "'";

			logger.info("Executing query " + sql);
			resultSet = stmt.executeQuery(sql);
			resultSet.next();
			int rows = resultSet.getInt(1);
			if (rows > 0) {
				logger.info(
						"This Service Now Request is already updated into database or matching entry does not exist");
				return true;
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			closeResources(resultSet, stmt);
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lunera.db.dao.ServiceNowDAO#updateServiceNowCount(com.lunera.dto.
	 * ServiceNowData)
	 */
	@Override
	public boolean updateServiceNowCount(ServiceNowData data) {
		PreparedStatement ps = null;
		// Hexa to decimal
		int deviceId = Integer.parseInt(data.getDeviceId(), 16);
		try {
			String sql = "UPDATE ServiceNowButtons SET TransId = ? , PressedCount = PressedCount + 1 WHERE Tenant_Id = ? AND ButtonId = ? AND Type = ? AND TransId != ? ";
			ps = dbConnection.getConnection().prepareStatement(sql);
			ps.setString(1, data.getTransID());
			ps.setString(2, data.getTenantid());
			ps.setInt(3, deviceId);
			ps.setString(4, data.getType());
			ps.setString(5, data.getTransID());
			logger.info("Executing query " + sql);
			int updateStatus = ps.executeUpdate();
			if (updateStatus > 0) {
				logger.info("ServiceNow Button Count has been updated");
				return true;
			}
			logger.info("This Service Now Request is already updated into database.");
		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			if (ps != null) {
				try {
					if (ps != null)
						ps.close();
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lunera.db.dao.ServiceNowDAO#getAllCustomer()
	 */
	@Override
	public List<LuneraCustomer> getAllCustomer() {
		Statement stmt = null;
		ResultSet resultSet = null;
		List<LuneraCustomer> customerList = new ArrayList<LuneraCustomer>();
		try {
			stmt = dbConnection.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			String sql = "select Id,Name from Customers";
			logger.info("Executing query " + sql);
			resultSet = stmt.executeQuery(sql);
			while (resultSet.next()) {
				LuneraCustomer customer = new LuneraCustomer();
				customer.setId(resultSet.getString("Id"));
				customer.setName(resultSet.getString("Name"));
				customerList.add(customer);
			}

		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			closeResources(resultSet, stmt);
		}
		return customerList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lunera.db.dao.ServiceNowDAO#getAllBuilding(java.lang.String)
	 */
	@Override
	public List<LuneraBuilding> getAllBuilding(String customerId) {
		Statement stmt = null;
		ResultSet resultSet = null;
		List<LuneraBuilding> buildingList = new ArrayList<LuneraBuilding>();
		try {
			stmt = dbConnection.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			String sql = "select Id,Name from Buildings where Customer_Id ='" + customerId + "'";
			logger.info("Executing query " + sql);
			resultSet = stmt.executeQuery(sql);
			while (resultSet.next()) {
				LuneraBuilding building = new LuneraBuilding();
				building.setId(resultSet.getString("Id"));
				building.setName(resultSet.getString("Name"));
				buildingList.add(building);
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			closeResources(resultSet, stmt);
		}
		return buildingList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lunera.db.dao.ServiceNowDAO#getAllFloors(java.lang.String)
	 */
	@Override
	public List<LuneraFloors> getAllFloors(String buildingId) {
		Statement stmt = null;
		ResultSet resultSet = null;
		List<LuneraFloors> floorList = new ArrayList<LuneraFloors>();
		try {
			stmt = dbConnection.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			String sql = "select Id,Name from Floors where Building_Id ='" + buildingId + "'";
			logger.info("Executing query " + sql);
			resultSet = stmt.executeQuery(sql);
			while (resultSet.next()) {
				LuneraFloors floors = new LuneraFloors();
				floors.setId(resultSet.getString("Id"));
				floors.setName(resultSet.getString("Name"));
				floorList.add(floors);
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			closeResources(resultSet, stmt);
		}
		return floorList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lunera.db.dao.ServiceNowDAO#getAllTenants(java.lang.String)
	 */
	@Override
	public List<LuneraTenants> getAllTenants(String floorId) {
		Statement stmt = null;
		ResultSet resultSet = null;
		List<LuneraTenants> tenantList = new ArrayList<LuneraTenants>();
		try {
			stmt = dbConnection.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			String sql = "select Id,Name from Tenants where Floor_Id ='" + floorId + "'";
			logger.info("Executing query " + sql);
			resultSet = stmt.executeQuery(sql);
			while (resultSet.next()) {
				LuneraTenants tenant = new LuneraTenants();
				tenant.setId(resultSet.getString("Id"));
				tenant.setName(resultSet.getString("Name"));
				tenantList.add(tenant);
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			closeResources(resultSet, stmt);
		}
		return tenantList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lunera.db.dao.ServiceNowDAO#resetCount(java.lang.String)
	 */
	@Override
	public boolean resetCount(String tenantId) {
		PreparedStatement ps = null;
		try {
			String sql = "UPDATE ServiceNowButtons SET PressedCount = 0 WHERE Tenant_Id = ? ";
			ps = dbConnection.getConnection().prepareStatement(sql);
			ps.setString(1, tenantId);

			logger.info("Executing query " + sql);
			int updateStatus = ps.executeUpdate();
			if (updateStatus > 0) {
				logger.info("ServiceNow Button Count has been reset :" + updateStatus);
				return true;
			}
			logger.info("Nothing to reset in service now button with this tenantId:" + tenantId);
		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			if (ps != null) {
				try {
					if (ps != null)
						ps.close();
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
		}
		return false;
	}
}
