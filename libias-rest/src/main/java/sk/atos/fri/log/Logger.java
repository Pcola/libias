package sk.atos.fri.log;

import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.atos.fri.dao.LogTypeStatus;
import sk.atos.fri.dao.libias.model.BamUser;
import sk.atos.fri.dao.libias.model.Log;
import sk.atos.fri.dao.libias.model.LogType;
import sk.atos.fri.dao.libias.service.LogService;
import sk.atos.fri.dao.libias.service.UserService;

/**
 *
 * @author Jaroslav Kollar
 */
@Service
public class Logger {
  @Autowired
  private UserService userService;
  
  @Autowired
  private LogService logService;
    
  @PersistenceContext(unitName = "libias-pu")
  private EntityManager entityManager;
  
  private static final String NO_USER = "ANONYM_USER";
  private org.slf4j.Logger LOG = null;    
  
  public Logger() {
    LOG = LoggerFactory.getLogger(Logger.class);
  }	

  public void debug(String message) {
    debug(NO_USER, message);
  }

  public void debug(String username, String message) { 
    if (username == null) {
      username = NO_USER;
    }
    LOG.debug(getCallerClassAndMethodName() + ", username: " + username + ", " + message);
  }

  public void info(String message) {
    info(NO_USER, message);
  }

  public void info(String username, String message) {  
    if (username == null) {
      username = NO_USER;
    }
    LOG.info(getCallerClassAndMethodName() + ", username: " + username + ", " + message);
  }
  
  public void warn(String message) {
    warn(NO_USER, message);
  }
  
  public void warn(String username, String message) {    
    if (username == null) {
      username = NO_USER;
    }
    LOG.warn(getCallerClassAndMethodName() + ", username: " + username + ", " + message);
  }
  
  public void error(Error errCode, Throwable thrwbl) {
    error(NO_USER, errCode, thrwbl);
  }	

  public void error(String username, Error errCode, Throwable thrwbl) {    
    if (username == null) {
      username = NO_USER;
    }
    LOG.error(getCallerClassAndMethodName() +
      ", username: " + username + ", " + errCode.getCode() + " - " + errCode.getDescription(), thrwbl);
  }

  public void error(Error errCode) {
    error(NO_USER, errCode, null);
  }

  public void error(String username, Error errCode) {    
    error(username, errCode, null);
  } 
  
  public void debugDB(String message, LogTypeStatus logTypeStatus) {
    debugDB(null, message, logTypeStatus);
  }

  public void debugDB(String username, String message, LogTypeStatus logTypeStatus) {  
    if (logService != null) {
      Log l = getDbLog(message, username, logTypeStatus, "DEBUG");
      logService.persist(l);
    }
  }

  public void infoDB(String message, LogTypeStatus logTypeStatus) {
    infoDB(null, message, logTypeStatus);
  }

  public void infoDB(String username, String message, LogTypeStatus logTypeStatus) {  
    if (logService != null) {
      Log l = getDbLog(message, username, logTypeStatus, "INFO");
      logService.persist(l);
    }
  }
  
  public void warnDB(String message, LogTypeStatus logTypeStatus) {
    warnDB(null, message, logTypeStatus);
  }
  
  public void warnDB(String username, String message, LogTypeStatus logTypeStatus) { 
    if (logService != null) {
      Log l = getDbLog(message, username, logTypeStatus, "WARN");
      logService.persist(l);
    }
  }
  
  public void errorDB(Error errCode, LogTypeStatus logTypeStatus) {
    errorDB(null, errCode, logTypeStatus);
  }	

  public void errorDB(String username, Error errCode, LogTypeStatus logTypeStatus) { 
    if (logService != null) {
      Log l = getDbLog(errCode.getCode() + " - " + errCode.getDescription(), username, logTypeStatus, "ERROR");
      logService.persist(l);
    }
  } 
  
  private String getCallerClassAndMethodName() {
    StackTraceElement stackTraceElements[] = Thread.currentThread().getStackTrace();
    if (stackTraceElements.length <= 1) {
      return "";
    }
           
    for (int i = 1; i < stackTraceElements.length; i++) {
      if (!stackTraceElements[i].getClassName().equals(Logger.class.getName())) {
        return stackTraceElements[i].getClassName() + ": " + stackTraceElements[i].getMethodName();        
      }
    }
    
    return "";
  }
  
  private Log getDbLog(String message, String username, LogTypeStatus logTypeStatus, String severity) {
    Log l = new Log(message, new Date(), username, entityManager.find(LogType.class, logTypeStatus.id),
      severity, getCallerClassAndMethodName());
    return l;
  }
}
