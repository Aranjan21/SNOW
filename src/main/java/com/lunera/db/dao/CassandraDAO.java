package com.lunera.db.dao;

import com.lunera.dto.ServiceNowData;

public interface CassandraDAO {
	public void saveServiceNowData(ServiceNowData data);
}
