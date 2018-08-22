package com.lunera.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.SemaphoreConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

@Configuration
public class HazelcastConfig {

	@Bean
	public HazelcastInstance initializeHazelInstance() {
		SemaphoreConfig semaphoreConfig = new SemaphoreConfig();
		semaphoreConfig.setName("reducer");
		semaphoreConfig.setInitialPermits(1);

		Config config = new Config();
		config.addSemaphoreConfig(semaphoreConfig);

		TcpIpConfig tcpIpConfig = new TcpIpConfig();
		tcpIpConfig.addMember("servicenow01.dev.lunera.com"); 
		tcpIpConfig.addMember("servicenow02.dev.lunera.com");
		tcpIpConfig.setEnabled(true);
		MulticastConfig multicastConfig = new MulticastConfig();
		multicastConfig.setEnabled(false);
		JoinConfig joinConfig = new JoinConfig();
		joinConfig.setMulticastConfig(multicastConfig);
		joinConfig.setTcpIpConfig(tcpIpConfig);
		NetworkConfig networkConfig = config.getNetworkConfig();
		networkConfig.setJoin(joinConfig);
		HazelcastInstance theHZInstance = Hazelcast.newHazelcastInstance(config);
		return theHZInstance;
	}
}
