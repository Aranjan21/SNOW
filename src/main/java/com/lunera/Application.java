package com.lunera;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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
 * This is main class which starts Service Now API server.
 * 
 * @author gautam.vijay added on 8 Aug 2018
 */
@EnableAsync
@SpringBootApplication
@ComponentScan(basePackages = { "com.lunera.db", "com.lunera.service", "com.lunera.controller", "com.lunera.config",
		"com.lunera.util.cache" })
public class Application {

	private final static Logger logger = LogManager.getLogger(Application.class);

	public static void main(String[] args) {
		loadLuneraPropertyFile();
		SpringApplication application = new SpringApplication(Application.class);
		application.run(args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	/**
	 * This method copy the lunera.properties file from /luner/etc to execution path
	 * with the name of application.properties. This property is automatically
	 * loaded into spring project.
	 */
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
