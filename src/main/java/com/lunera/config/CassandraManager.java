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

/**
 * This class contains query execution method of cassandra.
 * 
 * @author gautam.vijay
 *
 */
@Component
public class CassandraManager {

	@Autowired
	private Session session;

	public static long cassandraQueryTimeoutMs = 3000;

	private final static Logger logger = LogManager.getLogger(CassandraManager.class);

	/**
	 * This method executes queries with in time limit of 3second
	 * 
	 * @param query
	 * @return ResultSet
	 */
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
