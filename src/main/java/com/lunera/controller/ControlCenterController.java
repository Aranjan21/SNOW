package com.lunera.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.lunera.request.RawDataRequest;
import com.lunera.request.SummarizeDataRequest;
import com.lunera.response.RawDataResponse;
import com.lunera.response.SummarizeDataResponse;


/**
 * 
 * @author gautam.vijay added on 8 Aug 2018
 */
@RestController
@RequestMapping("/api/v1/serviceNow")
public class ControlCenterController {

	@RequestMapping(method = RequestMethod.POST, value = "/summarize", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<SummarizeDataResponse> getSummarizeData(SummarizeDataRequest summRequest) {
		System.out.println("Summarize data request :" + summRequest);
		return ResponseEntity.status(HttpStatus.OK).body(new SummarizeDataResponse());
	}

	@RequestMapping(method = RequestMethod.POST, value = "/details", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<RawDataResponse> getRawData(RawDataRequest rawRequest) {
		System.out.println("Raw Data Request:" + rawRequest);
		return ResponseEntity.status(HttpStatus.OK).body(new RawDataResponse());
	}
}
