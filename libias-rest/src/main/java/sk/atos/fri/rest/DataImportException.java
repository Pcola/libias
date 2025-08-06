package sk.atos.fri.rest;

/**
 *
 * @author kristian
 */
public class DataImportException extends Exception {

  public DataImportException() {
  }

  public DataImportException(String message) {
    super(message);
  }

  public DataImportException(String message, Throwable cause) {
    super(message, cause);
  }

  public DataImportException(Throwable cause) {
    super(cause);
  }

}
