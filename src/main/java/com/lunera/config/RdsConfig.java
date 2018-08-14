package com.lunera.config;

import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RdsConfig {

	private final static Logger logger = LogManager.getLogger(RdsConfig.class);

	@Value("${data.api.rds.username}")
	private String rdsUser;

	@Value("${data.api.rds.host}")
	private String rdsHost;

	@Value("${data.api.rds.port}")
	private String rdsPort;

	@Value("${data.api.rds.db_name}")
	private String rdsDbName;

	@Value("${data.api.rds.password}")
	private String rdsPass;

	@Bean
	public RdsConnection getConnection() {
		RdsConnection dbConn = new RdsConnection();
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String jdbcUrl = "jdbc:sqlserver://" + rdsHost + ":" + rdsPort + ";" + "databaseName=" + rdsDbName
					+ ";user=" + rdsUser + ";password=" + rdsPass;
			logger.trace("connecting to " + rdsHost + ":" + rdsPort);
			dbConn.setConnection(DriverManager.getConnection(jdbcUrl));
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage());
		} catch (SQLException e) {
			logger.error("ErrorCode " + e.getErrorCode() + ": " + e.getMessage());
			System.exit(e.getErrorCode());
		}
		return dbConn;
	}
}
