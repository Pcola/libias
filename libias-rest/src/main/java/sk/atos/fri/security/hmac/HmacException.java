package sk.atos.fri.security.hmac;

/**
 * Hmac signer exception.
 */
public class HmacException extends Exception {

  public HmacException(String message) {
    super(message);
  }

  public HmacException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
