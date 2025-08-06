package sk.atos.fri.rest.service;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import sk.atos.fri.dao.libias.service.DbIdentService;
import sk.atos.fri.dataImport.IDataImport;
import sk.atos.fri.log.Logger;
import sk.atos.fri.rest.DataImportException;
import sk.atos.fri.rest.model.JobStatusResponse;
import sk.atos.fri.rest.model.WSTestRequest;
import sk.atos.fri.ws.maris.model.*;
import sk.atos.fri.ws.maris.service.MarisWSClient;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(path = "/dataImport")
public class DataImportController {

  @Autowired
  private Logger LOG; 

  @Autowired
  private MarisWSClient marisClient;

  @Autowired
  private DbIdentService dbIdentService;

  @Autowired
  private TaskExecutor taskExecutor;

  @Autowired
  private IDataImport importTask;

  @RequestMapping(path = "/start",
                  method = RequestMethod.POST,
                  consumes = MediaType.APPLICATION_JSON_VALUE,
                  produces = MediaType.APPLICATION_JSON_VALUE)
  //@Secured({Constants.ROLE_ADMIN})
  public DeferredResult<JobStatusResponse> startImport(HttpServletRequest httpServletRequest) throws DataImportException {
    DeferredResult<JobStatusResponse> deferredResult = new DeferredResult<>();
    if(importTask.isRunning()) {
    	JobStatusResponse resp = new JobStatusResponse();
    	resp.setErrorMessage("Import Job is already running. Current state: "+importTask.getStatus().getJobStatus());
    	deferredResult.setResult(resp);
    } else {
		importTask.willSkipMarisImport(false);
    	CompletableFuture<Void> future = CompletableFuture.runAsync(importTask);
    	future.whenComplete((Void, error)->{deferredResult.setResult(importTask.getStatus());});
    }
    return deferredResult;
  }
  
  @RequestMapping(path = "/start/skipMaris",
          method = RequestMethod.POST,
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  //@Secured({Constants.ROLE_ADMIN})
  public DeferredResult<JobStatusResponse> startImportSkipMaris(HttpServletRequest httpServletRequest) throws DataImportException {
    DeferredResult<JobStatusResponse> deferredResult = new DeferredResult<>();
		if (importTask.isRunning()) {
			JobStatusResponse resp = new JobStatusResponse();
			resp.setErrorMessage("Import Job is already running. Current state: " + importTask.getStatus().getJobStatus());
			deferredResult.setResult(resp);
		} else {
			importTask.willSkipMarisImport(true);
			CompletableFuture<Void> future = CompletableFuture.runAsync(importTask);
			future.whenComplete((Void, error) -> {
				deferredResult.setResult(importTask.getStatus());
			});
		}
		return deferredResult;
	}

  @RequestMapping(path = "/jobStatus",
                  method = RequestMethod.GET,
                  consumes = MediaType.APPLICATION_JSON_VALUE,
                  produces = MediaType.APPLICATION_JSON_VALUE)
  //@Secured({Constants.ROLE_ADMIN})
  public JobStatusResponse getJobStatus() {
      return importTask.getStatus();
  }
  
  @RequestMapping(path = "/bild",
                  method = RequestMethod.POST,
                  consumes = MediaType.APPLICATION_JSON_VALUE,
                  produces = MediaType.APPLICATION_JSON_VALUE)  
  public Void getBild(@RequestBody WSTestRequest request, HttpServletRequest httpServletRequest) throws DataImportException {
    LOG.debug(ToStringBuilder.reflectionToString(request, ToStringStyle.JSON_STYLE));
    PersonResponse response = marisClient.getPerson(request.getBildId());
    if (response != null) {
      LOG.debug(response.toString());
    }
    else {
      LOG.debug("Response is null");
    }
    return null;
  }
  
  @RequestMapping(path = "/bildPer",
                  method = RequestMethod.POST,
                  consumes = MediaType.APPLICATION_JSON_VALUE,
                  produces = MediaType.APPLICATION_JSON_VALUE)  
  public Void getBildPer(@RequestBody WSTestRequest request, HttpServletRequest httpServletRequest) throws DataImportException {
    if (request.getBildId() == -1L) {
      LOG.debug("Start enriching database identification results");
      dbIdentService.enrichDbIdentResults(httpServletRequest);
      LOG.debug("Finished enriching database identification results");
      return null;
    }
    
    LOG.debug(ToStringBuilder.reflectionToString(request, ToStringStyle.JSON_STYLE));    
    
    LOG.debug("Start bild performance test for bild id: " + request.getBildId());
    for (int i = 0; i < 100; i++) {      
      MarisCallBildPerTask myThread = new MarisCallBildPerTask(i, marisClient, request.getBildId());       
      taskExecutor.execute(myThread);        
    }
    
    return null;
  }
  
  @RequestMapping(path = "/akte",
                  method = RequestMethod.POST,
                  consumes = MediaType.APPLICATION_JSON_VALUE,
                  produces = MediaType.APPLICATION_JSON_VALUE)  
  public Void getAkte(@RequestBody WSTestRequest request, HttpServletRequest httpServletRequest) throws DataImportException {
    LOG.debug(ToStringBuilder.reflectionToString(request, ToStringStyle.JSON_STYLE));    
    AkteResponse response = marisClient.getAkte(request.getAktenzeichenA(), request.getAktenzeichenB());
    if (response != null) {
      if (response.getRecords() != null) {
        for (Record record : response.getRecords()) {
          LOG.debug(record.toString());
        }
      }
      else {
        LOG.debug("Response.Records is null");
      }
    }
    else {
      LOG.debug("Response is null");
    }
    
    return null;
  }
  
  @RequestMapping(path = "/aktePer",
                  method = RequestMethod.POST,
                  consumes = MediaType.APPLICATION_JSON_VALUE,
                  produces = MediaType.APPLICATION_JSON_VALUE)  
  public Void getAktePer(@RequestBody WSTestRequest request, HttpServletRequest httpServletRequest) throws DataImportException {
    if ("-1".equals(request.getAktenzeichenA()) && "-1".equals(request.getAktenzeichenB())) {
      LOG.debug("Start exporting database identification results");
      dbIdentService.exportDbIdentResults(httpServletRequest);
      LOG.debug("Finished exporting database identification results");
      return null;
    }
    
    LOG.debug(ToStringBuilder.reflectionToString(request, ToStringStyle.JSON_STYLE));
    
    LOG.debug("Start akte performance test for aktenzeichen: " + request.getAktenzeichenA() + ", " + request.getAktenzeichenB());
    for (int i = 0; i < 100; i++) {
      MarisCallAktePerTask myThread = new MarisCallAktePerTask(i, marisClient, request.getAktenzeichenA(), request.getAktenzeichenB());       
      taskExecutor.execute(myThread);    
    }
    
    return null;
  }
  
  @RequestMapping(path = "/getUpdatedApplicants",
                  method = RequestMethod.POST,
                  consumes = MediaType.APPLICATION_JSON_VALUE,
                  produces = MediaType.APPLICATION_JSON_VALUE)  
  public Void getUpdatedApplicants(@RequestBody WSTestRequest request, HttpServletRequest httpServletRequest) throws DataImportException {
    LOG.debug("getUpdatedApplicants start.");
    UpdatedApplicants response = marisClient.getUpdatedApplicants();        
    if (response != null) {
      if (response.getUpdatedApplicants() != null) {
        for (UpdatedApplicant applicant : response.getUpdatedApplicants()) {
          LOG.debug(applicant.toString());
        }
      }
      else {
        LOG.debug("Response.UpdatedApplicants is null");
      }
    }
    else {
      LOG.debug("Response is null");
    }
    
    return null;
  }
  
  @RequestMapping(path = "/getToken",
                  method = RequestMethod.GET,                  
                  produces = MediaType.APPLICATION_JSON_VALUE)    
  public Void getToken(HttpServletRequest httpServletRequest) throws DataImportException {
    LOG.debug("getToken start.");
    String token = marisClient.getToken().getAccessToken();
    LOG.debug("Token result: " + token);
    
    return null;
  }
}
