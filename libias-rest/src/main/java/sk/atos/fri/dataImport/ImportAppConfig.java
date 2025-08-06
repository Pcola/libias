package sk.atos.fri.dataImport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sk.atos.fri.configuration.ServerConfig;

import java.util.regex.Pattern;

@Configuration
public class ImportAppConfig {

	@Autowired
	private ServerConfig serverConfig;

	@Bean
	public SchedulerConfig schedulerConfig() {
		SchedulerConfig schedulerConfig = new SchedulerConfig();

		Pattern p = Pattern.compile("\\d{2}:\\d{2}:\\d{2}");
		if (p.matcher(serverConfig.getSchedulerImportRunAt()).matches()) {
			schedulerConfig.setExecutionTime(serverConfig.getSchedulerImportRunAt());
		}

		return schedulerConfig;
	}

	@Bean
	public IDataImport dataImport(ImportTask dataImport) {
		dataImport.willSkipDeleteOverWs(serverConfig.getSchedulerImportSkipDeleteOverWs());
		return dataImport;
	}

}
