package com.lunera.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.lunera.db.rds.dao.ServiceNowDAO;
import com.lunera.dto.ServiceNowData;
import com.lunera.util.cache.CacheKey;
import com.lunera.util.cache.CacheValue;
import com.lunera.util.cache.ServiceNowCache;
import com.lunera.util.enums.ApplicationConstants;
import com.lunera.util.enums.ServiceNowConstants;

/**
 * 
 * @author gautam.vijay added on 8 Aug 2018
 */

@Service
public class ServiceNowImpl implements ServiceNow {

	private final static Logger logger = LogManager.getLogger(ServiceNowImpl.class);

	@Autowired
	private ServiceNowDAO rds;

	@Autowired
	private ServiceNowCache serviceNowCache;

	@Override
	public void processServiceNowRequest(MultiValueMap<String, String> requestData) {
		ServiceNowData data = buildServiceNowData(requestData);
		CacheKey cacheKey = buildCacheKey(data);
		CacheValue cacheValue = serviceNowCache.get(cacheKey);
		DateTime publishTime = new DateTime(data.getPublished_at());
		if (cacheValue == null) {
			cacheValue = new CacheValue();
			cacheValue.setTimeStamp(publishTime);
			serviceNowCache.put(cacheKey, cacheValue);
			updateServiceNowCount(data);
		} else if (cacheValue.getTimeStamp()
				.isBefore(DateTime.now().minusMinutes(ApplicationConstants.MAX_DUPLICATE_MSG_INTERVAL_MINUTES))) {
			logger.info("Last Published Time :" + cacheValue.getTimeStamp() + " Current Time: " + DateTime.now());
			cacheValue.setTimeStamp(publishTime);
			serviceNowCache.put(cacheKey, cacheValue);
			updateServiceNowCount(data);
		} else {
			logger.info("Last Published Time :" + cacheValue.getTimeStamp() + " Current Time: " + DateTime.now());
			logger.info("Duplicate serviceNow event received" + cacheKey);
		}
		logger.info("Service now data with complete details" + data);
	}

	private void updateServiceNowCount(ServiceNowData data) {
		// First Check that data already updated or not(tenantId, transID,buttonId,type)
		// If already updated then ignore else update count
		if (!rds.isServiceCountAlreadyUpdated(data)) {
			rds.updateServiceNowCount(data);
			// Need to update cassandra raw table
		}
	}

	private CacheKey buildCacheKey(ServiceNowData serviceNowData) {
		CacheKey key = new CacheKey();
		key.setCustomerid(serviceNowData.getCustomerid());
		key.setDeviceId(serviceNowData.getDeviceId());
		key.setTransID(serviceNowData.getTransID());
		key.setType(serviceNowData.getType());
		return key;
	}

	public ServiceNowData buildServiceNowData(MultiValueMap<String, String> requestData) {
		ServiceNowData serviceNowData = new ServiceNowData();

		serviceNowData.setCoreid(requestData.get(ServiceNowConstants.coreid.name()).get(0));
		serviceNowData.setId(requestData.get(ServiceNowConstants.id.name()).get(0));
		serviceNowData.setEvent(requestData.get(ServiceNowConstants.event.name()).get(0));
		serviceNowData.setPublished_at(requestData.get(ServiceNowConstants.published_at.name()).get(0));
		serviceNowData.setTtl(requestData.get(ServiceNowConstants.ttl.name()).get(0));
		String data = requestData.get(ServiceNowConstants.data.name()).get(0);
		parseDataAndUpdate(data, serviceNowData);

		// Fetch All customer details using lamp id
		Map<String, String> customerDetails = rds.findContainerIdsbyLampSerialNumber(serviceNowData.getCoreid());
		serviceNowData.setCustomerid(customerDetails.get(ServiceNowConstants.customerid.name()));
		serviceNowData.setBuildingid(customerDetails.get(ServiceNowConstants.buildingid.name()));
		serviceNowData.setFloorid(customerDetails.get(ServiceNowConstants.floorid.name()));
		serviceNowData.setTenantid(customerDetails.get(ServiceNowConstants.tenantid.name()));
		return serviceNowData;
	}

	public void parseDataAndUpdate(String data, ServiceNowData serviceNowData) {
		Map<String, String> dataMap = new HashMap<String, String>();
		String keyValues[] = data.split(",");
		for (String keyValue : keyValues) {
			String detail[] = keyValue.split("=");
			dataMap.put(detail[0], detail[1]);
		}
		serviceNowData.setDeviceId(dataMap.get(ServiceNowConstants.deviceId.name()));
		serviceNowData.setType(dataMap.get(ServiceNowConstants.type.name()));
		serviceNowData.setTransID(dataMap.get(ServiceNowConstants.transID.name()));
	}
}
