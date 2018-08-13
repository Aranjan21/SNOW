package com.lunera;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

import com.lunera.util.enums.ApplicationConstants;



/**
 * 
 * @author gautam.vijay added on 8 Aug 2018
 */
@EnableAsync
@SpringBootApplication
@ComponentScan(basePackages = { "com.lunera.db.rds", "com.lunera.service", "com.lunera.controller",
		"com.lunera.db.config", "com.lunera.util.cache" })
public class Application {

	private final static Logger logger = LogManager.getLogger(Application.class);

	public static void main(String[] args) {
		loadLuneraPropertyFile();
		// SpringApplication.run(Application.class, args);
		SpringApplication application = new SpringApplication(Application.class);
		HashMap<String, Object> defaultProperties = new HashMap<>();
		defaultProperties.put("server.port", 6001);
		application.setDefaultProperties(defaultProperties);
		application.run(args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	public static void loadLuneraPropertyFile() {
		String propConfig = System.getProperty(ApplicationConstants.ENVIRONMENT_VARIABLE);
		String configPathStr = (propConfig != null && !propConfig.trim().isEmpty()) ? propConfig
				: System.getenv(ApplicationConstants.ENVIRONMENT_VARIABLE);
		Path fromPath = null;
		if (configPathStr == null || configPathStr.trim().isEmpty()) {
			fromPath = ApplicationConstants.DEFAULT_PROPERTIES_LOCATION;
		} else {
			fromPath = Paths.get(configPathStr);
		}
		ClassPathResource cpr = new ClassPathResource("application.properties");
		Path toPath = Paths.get(cpr.getFilename());
		try {
			logger.info("Copying lunera property file from:+" + fromPath + " toPath:" + toPath);
			Files.copy(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			logger.error("Exception Occurred durring copying lunera.property file: ");
			e.printStackTrace();
		}
	}
}
