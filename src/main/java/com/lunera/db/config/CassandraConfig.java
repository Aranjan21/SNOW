//package com.lunera.db.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import com.datastax.driver.core.Cluster;
//import com.datastax.driver.core.Session;
//
//@Configuration
//public class CassandraConfig {
//	
//@Bean
//public Session getSession(){
//		String[] cassandraHost = new String[] {"127.0.0.1"};
//		String clusterName = "service_now";
//		Cluster cluster = Cluster.builder()
//                .addContactPoints(cassandraHost)
//                .withPort(9042)
//                .withClusterName(clusterName)
//                .build();
//    	Session session = cluster.connect();
//    	return session;
//	}
//	
//}
