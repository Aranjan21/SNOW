package com.lunera.db.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lunera.config.RdsConnection;
import com.lunera.dto.ServiceNowData;

@Service
public class ServiceNowDAOImpl implements ServiceNowDAO {

	private final static Logger logger = LogManager.getLogger(ServiceNowDAOImpl.class);

	@Autowired
	private RdsConnection dbConnection;

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

	@Override
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
				logger.info("This Service Now Request is already updated into database.");
				return true;
			}
		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			closeResources(resultSet, stmt);
		}

		return false;
	}

	@Override
	public void updateServiceNowCount(ServiceNowData data) {
		PreparedStatement ps = null;
		// Hexa to decimal
		int deviceId = Integer.parseInt(data.getDeviceId(), 16);
		try {
			String sql = "UPDATE ServiceNowButtons SET TransId = ?, PressedCount = PressedCount + 1 WHERE Tenant_Id = ? AND ButtonId = ? AND Type = ?";
			ps = dbConnection.getConnection().prepareStatement(sql);
			ps.setString(1, data.getTransID());
			ps.setString(2, data.getTenantid());
			ps.setInt(3, deviceId);
			ps.setString(4, data.getType());
			logger.info("Executing query " + sql);
			int updateStatus = ps.executeUpdate();
			if (updateStatus > 0) {
				logger.info("ServiceNow Button Count has been updated");
			}
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
	}
}
