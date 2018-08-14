package com.lunera.config;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Session;

@Component
public class CassandraManager {

	@Autowired
	private Session session;

	public static long cassandraQueryTimeoutMs = 3000;

	private final static Logger logger = LogManager.getLogger(CassandraManager.class);

	public ResultSet executeSynchronously(String query) {
		ResultSetFuture future = this.session.executeAsync(query);
		logger.debug(query);
		try {
			return future.get(cassandraQueryTimeoutMs, TimeUnit.MILLISECONDS);
		} catch (InterruptedException | ExecutionException e) {
			logger.error("problem executing query " + query, e);
		} catch (TimeoutException e) {
			logger.error("query took > " + cassandraQueryTimeoutMs + "ms: " + query);
		}
		logger.warn("returning null query results");
		return null;
	}
}
