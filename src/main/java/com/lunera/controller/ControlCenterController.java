package com.lunera.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.lunera.request.RawDataRequest;
import com.lunera.request.SummarizeDataRequest;
import com.lunera.response.RawDataResponse;
import com.lunera.response.SummarizeDataResponse;
import com.lunera.service.ControlCenterService;

/**
 * 
 * @author gautam.vijay added on 8 Aug 2018
 */
@RestController
@RequestMapping("/api/v1/serviceNow")
public class ControlCenterController {

	@Autowired
	private ControlCenterService controlCenterService;

	@RequestMapping(method = RequestMethod.POST, value = "/summarize", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<SummarizeDataResponse> getSummarizeData(@RequestBody SummarizeDataRequest summaryDataRequest)
			throws IOException {
		System.out.println("Summarize data request :" + summaryDataRequest);
		return ResponseEntity.status(HttpStatus.OK).body(controlCenterService.getSummaryData(summaryDataRequest));
	}

	@RequestMapping(method = RequestMethod.POST, value = "/details", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<RawDataResponse> getRawData(@RequestBody RawDataRequest rawDataRequest) {
		System.out.println("Raw Data Request:" + rawDataRequest);
		return ResponseEntity.status(HttpStatus.OK).body(controlCenterService.getRawData(rawDataRequest));
	}
}
