package com.lunera.service.reducer;

import java.util.Date;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ISemaphore;
import com.lunera.db.dao.ServiceNowDAO;
import com.lunera.dto.LuneraBuilding;
import com.lunera.dto.LuneraCustomer;
import com.lunera.util.enums.ApplicationConstants;

@Component
public class SchedulerService {

	private final static Logger logger = LogManager.getLogger(SchedulerService.class);

	@Autowired
	private HazelcastInstance hazelcastInstance;

	@Autowired
	private ServiceNowDAO serviceNowDAO;

	@Autowired
	private ServiceNowDataReducer serviceNowDataReducer;

	@Scheduled(fixedDelay = 10 * 60 * 1000)
	public void startReducer() {
		logger.info("SchedulerServiceExecution:Service Now Reducer started: " + new Date());
		ISemaphore semaphore = hazelcastInstance.getSemaphore("reducer");

		boolean acquired = semaphore.tryAcquire(1);

		if (acquired) {
			try {
				List<LuneraCustomer> customers = serviceNowDAO.getAllCustomer();
				customers.forEach(customer -> {
					logger.info("Reducing customer " + customer + " at " + ApplicationConstants.df.format(new Date()));
					reduceCustomer(customer);
				});
			} finally {
				semaphore.release();
			}
		} else {
			logger.info("Could not acquire semaphore");
		}

		logger.info("SchedulerServiceExecution:Service Now Reducer completed: " + new Date());
	}

	/**
	 * This method reducer customer into building. Fetch all the buildings and start
	 * building reducer.
	 * 
	 * @param customer
	 */
	private void reduceCustomer(LuneraCustomer customer) {
		List<LuneraBuilding> buildings = serviceNowDAO.getAllBuilding(customer.getId());
		buildings.forEach(building -> {
			logger.info("Reducing building :" + building + "of customer :" + customer.getName());
			serviceNowDataReducer.reduceBuilding(building);
		});
	}
}
