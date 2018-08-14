package com.lunera.service;

import org.springframework.util.MultiValueMap;

import com.lunera.response.ServiceNowResponse;

/**
 * 
 * @author gautam.vijay added on 8 Aug 2018
 */
public interface ServiceNow {
	public ServiceNowResponse processServiceNowRequest(MultiValueMap<String, String> requestData);
}
