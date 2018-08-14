package com.lunera.db.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lunera.config.CassandraManager;
import com.lunera.dto.ServiceNowData;

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

}
