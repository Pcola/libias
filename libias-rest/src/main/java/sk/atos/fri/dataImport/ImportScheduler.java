package sk.atos.fri.dataImport;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import sk.atos.fri.log.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class ImportScheduler implements ApplicationListener<ContextRefreshedEvent>, DisposableBean {

	private final Logger LOG = new Logger();

	@Autowired
	private IDataImport importTask;

	@Autowired
	private SchedulerConfig schedulerConfig;

	private ScheduledExecutorService scheduler;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		LOG.info("SCHEDULER : IS CONFIGURED : " + schedulerConfig.isSchedulerConfigured());
		try {
			if (schedulerConfig.isSchedulerConfigured()) {
				long initialDelay = schedulerConfig.getMilisecondsLeftToNextImportExecution();
				long period = 24 * 60 * 60 * 1000;
				scheduler = Executors.newSingleThreadScheduledExecutor();
				scheduler.scheduleAtFixedRate(importTask, initialDelay, period, TimeUnit.MILLISECONDS);
				LOG.info("SCHEDULER : FIRST EXECUTION IN " + initialDelay + " ms");
				LOG.info("SCHEDULER : IMPORT TASK WILL BE EXECUTED EVERY DAY AT " + schedulerConfig.getRunAtTime());
			}
		} catch (Exception e) {
			LOG.info("SCHEDULER : FAILED SCHEDULING. error: " + e.getMessage());
		}
	}

	@Override
	public void destroy() {
		if (scheduler != null) {
			scheduler.shutdownNow();
		}
	}
}
