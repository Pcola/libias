package sk.atos.fri.dataImport;

import java.util.Calendar;
import java.util.Date;

import sk.atos.fri.log.Logger;

public class SchedulerConfig {

	private String execTime;

	private final Logger LOG = new Logger();

	public void setExecutionTime(String time) {
		execTime = time;
	}

	public boolean isSchedulerConfigured() {
		if (execTime == null) {
			return false;
		}
		return true;
	}

	public String getRunAtTime() {
		return execTime;
	}

	public long getMilisecondsLeftToNextImportExecution() {
		int hour;
		int minute;
		int second;

		if (execTime == null) {
			return 0l;
		}
		String[] hms = execTime.split(":");
		hour = Integer.parseInt(hms[0]);
		minute = Integer.parseInt(hms[1]);
		second = Integer.parseInt(hms[2]);

		if ((hour > 23 && hour < 0) || (minute > 59 && minute < 0) || (second > 59 && second < 0)) {
			execTime = null;
			return 0l;
		} else {
			Calendar calendar = Calendar.getInstance();

			calendar.set(Calendar.HOUR_OF_DAY, hour);
			calendar.set(Calendar.MINUTE, minute);
			calendar.set(Calendar.SECOND, second);

			long currentTime = new Date().getTime();
			long calendarTime = calendar.getTime().getTime();

			if (calendarTime < currentTime) {
				calendar.add(Calendar.HOUR_OF_DAY, 24);
			}

			return (calendar.getTime().getTime() - currentTime);
		}
	}

}
