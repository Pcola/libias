package sk.atos.fri.rest.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class JobStatusResponse {
  private String jobStatus;
  private Date jobStarted;
  private Date jobFinished;
  private String errorMessage;
  private int progress;

  public JobStatusResponse() {
  }

  public JobStatusResponse(String jobStatus, Date jobStarted, Date jobFinished, int progress) {
    this.jobStatus = jobStatus;
    this.jobStarted = jobStarted;
    this.jobFinished = jobFinished;
    this.progress = progress;
  }

  public String getJobStatus() {
    return jobStatus;
  }

  public void setJobStatus(String jobStatus) {
    this.jobStatus = jobStatus.toUpperCase();
  }

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss", timezone = "Europe/Berlin")
  public Date getJobStarted() {
    return jobStarted;
  }

  public void setJobStarted(Date jobStarted) {
    this.jobStarted = jobStarted;
  }

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss", timezone = "Europe/Berlin")
  public Date getJobFinished() {
    return jobFinished;
  }

  public void setJobFinished(Date jobFinished) {
    this.jobFinished = jobFinished;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

	public int getProgress() {
		return progress;
	}
	
	public void setProgress(int progress) {
		this.progress = progress;
	}
	
  public void reset() {
	    this.jobStatus = null;
	    this.jobStarted = null;
	    this.jobFinished = null;
	    this.progress = 0;
	    this.errorMessage = null;
  }

}
