package com.lunera.controller;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

//import com.lunera.db.rds.connection.ServiceNowDAO;
import com.lunera.service.ServiceNow;

/**
 * 
 * @author gautam.vijay added on 8 Aug 2018
 */
@RestController
@RequestMapping("/particlehook/")
public class ServiceNowController {

	private final static Logger logger = LogManager.getLogger(ServiceNowController.class);

	@Autowired
	private ServiceNow serviceNow;

	@RequestMapping(method = RequestMethod.POST, value = "/luneraapp", consumes = {
			MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public void webhook(@RequestBody MultiValueMap<String, String> requestData) {
		logger.info("Request received from Lamp [/particlehook/luneraapp] : " + requestData);
		serviceNow.processServiceNowRequest(requestData);
		logger.info("Request completed from Lamp [/particlehook/luneraapp] : " + requestData);
	}
}
