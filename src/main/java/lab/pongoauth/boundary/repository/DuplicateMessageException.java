package lab.pongoauth.boundary.repository;

/**
 * DuplicateMessageException.
 */
public class DuplicateMessageException extends Exception {

  private static final long serialVersionUID = 1L;

  public DuplicateMessageException(String message, Throwable cause) {
    super(message, cause);
  }
}