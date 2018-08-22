package com.lunera.util.rest;

import java.util.Arrays;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class ApplicationRestClient {

	private final static Logger logger = LogManager.getLogger(ApplicationRestClient.class);

	@Autowired
	private RestTemplate restTemplate;

	private <T> ResponseEntity<T> executeWithoutOauth(String uri, HttpMethod method, Object request,
			Class<T> responseType) {
		HttpHeaders headers = createDefaultHeader();
		Object requestBody = request;
		if (request instanceof HttpEntity) {
			requestBody = ((HttpEntity<?>) request).getBody();
			headers.putAll(((HttpEntity<?>) request).getHeaders());
		}
		try {
			return restTemplate.exchange(uri, method, new HttpEntity<>(requestBody, headers), responseType);
		} catch (HttpClientErrorException e) {
			logger.error(Thread.currentThread().getStackTrace()[1].getMethodName() + " url=" + uri + " Status = "
					+ e.getStatusText() + " Cause=" + e.getCause() + " ResponseBody=" + e.getResponseBodyAsString());
			throw e;
		}
	}

	private HttpHeaders createDefaultHeader() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		return headers;
	}

}
