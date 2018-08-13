package com.lunera.service;

import org.springframework.util.MultiValueMap;

/**
 * 
 * @author gautam.vijay added on 8 Aug 2018
 */
public interface ServiceNow {
	public void processServiceNowRequest(MultiValueMap<String, String> requestData);
}
