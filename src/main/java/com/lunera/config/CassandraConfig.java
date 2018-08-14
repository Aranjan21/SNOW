package com.lunera.config;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.lunera.Application;

@Configuration
public class CassandraConfig {

	@Value("${data.api.db.host}")
	private String host;
	
	@Value("${data.api.db.port}")
	private int port;
	
	@Value("${data.api.db.name}")
	private String clusterName;

	
	private final static Logger logger = LogManager.getLogger(Application.class);

	@Bean
	public Session getSession() {
		String[] cassandraHost = new String[] { host };
		Cluster cluster = Cluster.builder().addContactPoints(cassandraHost).withPort(port).withClusterName(clusterName)
				.build();
		Session session = cluster.connect();
		createkeyspacesAndTables(session);
		return session;
	}

	private void createkeyspacesAndTables(Session session) {
		logger.info("Creating Keyspaces & Tables if not exist....");
		session.execute("CREATE KEYSPACE if not exists " + clusterName
				+ " WITH REPLICATION = {'class' : 'SimpleStrategy', 'replication_factor' : 1 };");
		session.execute("use " + clusterName);

		session.execute("create table if not exists service_now ("
		+ "buildingId text,"
		+ "buttonId int,"
		+ "serviceType int,"
		+ "transId int,"
		+ "timestamp timestamp,"
		+ "primary key(buildingId,buttonId,serviceType,transId))"
		+ "with clustering order by(buttonId asc, serviceType asc, transId asc)");;
		
		session.execute("create table if not exists service_now_hour (" 
		+ "buildingId text,"
		+ "buttonId int,"
		+ "serviceType int," 
		+ "transId int," 
		+ "fromDate timestamp," 
		+ "toDate timestamp,"
		+ "primary key(buildingId,buttonId,serviceType))"
		+ "with clustering order by(buttonId asc, serviceType asc)");
		
		session.execute("create table if not exists service_now_day (" 
		+ "buildingId text,"
		+ "buttonId int,"
		+ "serviceType int,"
		+ "transId int,"
		+ "fromDate timestamp,"
		+ "toDate timestamp,"
		+ "count int,"
		+ "primary key(buildingId, buttonId, serviceType))"
		+ "with clustering order by(buttonId asc, serviceType asc)");
	}
}
