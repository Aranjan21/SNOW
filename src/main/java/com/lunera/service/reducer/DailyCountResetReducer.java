package com.lunera.service.reducer;

import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DailyCountResetReducer {
	private final static Logger logger = LogManager.getLogger(DailyCountResetReducer.class);

	@Scheduled(cron = "0 0 0 * * ?")
	public void startReducer() {
		logger.info("Execution started: " + new Date());
		
		logger.info("Execution completed: " + new Date());
	}
}
