package com.lunera.config;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.lunera.Application;

/**
 * This class defines all DB configuration of cassandra and also initiate
 * session object.
 * 
 * @author gautam.vijay
 *
 */
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
		String[] cassandraHost = host.split(",");
		Cluster cluster = Cluster.builder().addContactPoints(cassandraHost).withPort(port).withClusterName(clusterName)
				.build();
		Session session = cluster.connect();
		createkeyspacesAndTables(session);
		return session;
	}

	/**
	 * This method creates key space & tables first time if not created.
	 * 
	 * @param session
	 */
	private void createkeyspacesAndTables(Session session) {
		logger.info("Creating Keyspaces & Tables if not exist....");
		session.execute("CREATE KEYSPACE if not exists " + clusterName
				+ " WITH REPLICATION = {'class' : 'SimpleStrategy', 'replication_factor' : 1 };");
		session.execute("use " + clusterName);

		session.execute(
				"create table if not exists service_now (" + "buildingId text," + "buttonId text," + "serviceType int,"
						+ "transId int," + "timestamp timestamp," + "primary key(buildingId,timestamp,buttonId))"
						+ "with clustering order by(timestamp desc, buttonId desc)");

		session.execute("create table if not exists service_now_summary_hour (" + "buildingId text," + "totalHappy int,"
				+ "totalSad int," + "totalService int," + "timestamp timestamp,"
				+ "primary key(buildingId,timestamp))" + "with clustering order by(timestamp desc)");

		session.execute("create table if not exists service_now_summary_day (" + "buildingId text," + "totalHappy int,"
				+ "totalSad int," + "totalService int," + "timestamp timestamp,"
				+ "primary key(buildingId,timestamp))" + "with clustering order by(timestamp desc)");
	}
}
