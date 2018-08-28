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
import com.lunera.dto.LuneraFloors;
import com.lunera.dto.LuneraTenants;

@Component
public class CountResetSchedulerService {

	private final static Logger logger = LogManager.getLogger(CountResetSchedulerService.class);

	@Autowired
	private HazelcastInstance hazelcastInstance;

	@Autowired
	private ServiceNowDAO serviceNowDAO;

	@Scheduled(cron = "0 0 0 * * ?")
	public void countResetService() {
		logger.info("Count reset scheduler execution started:" + new Date());
		ISemaphore semaphore = hazelcastInstance.getSemaphore("countReset");
		boolean acquired = semaphore.tryAcquire(1);

		if (acquired) {
			try {
				List<LuneraCustomer> customers = serviceNowDAO.getAllCustomer();
				customers.forEach(customer -> {
					List<LuneraBuilding> buildings = serviceNowDAO.getAllBuilding(customer.getId());
					buildings.forEach(building -> {
						List<LuneraFloors> floors = serviceNowDAO.getAllFloors(building.getId());
						floors.forEach(floor -> {
							List<LuneraTenants> tenants = serviceNowDAO.getAllTenants(floor.getId());
							tenants.forEach(tenant -> {
								logger.info("Count reset for :" + customer.getId() + ":" + building.getId() + ":"
										+ floor.getId() + ":" + tenant.getId());
								serviceNowDAO.resetCount(tenant.getId());
							});
						});
					});
				});
			} finally {
				semaphore.release();
			}
		} else {
			logger.info("Could not acquire semaphore for count reset scheduler");
		}

		logger.info("Count reset scheduler execution completed:" + new Date());
	}
}
