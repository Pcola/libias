package sk.atos.fri.dataImport;

import sk.atos.fri.rest.model.JobStatusResponse;

public interface IDataImport extends Runnable {

	public abstract void run();

	public abstract JobStatusResponse getStatus();

	public abstract boolean isRunning();

	public abstract void willSkipMarisImport(boolean skip);

	public abstract void willSkipDeleteOverWs(boolean skip);

}
