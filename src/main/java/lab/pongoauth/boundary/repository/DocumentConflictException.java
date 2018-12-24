package lab.pongoauth.boundary.repository;

public class DocumentConflictException extends Exception {

  private static final long serialVersionUID = 1L;

  public DocumentConflictException(String message, Throwable cause) {
    super(message, cause);
  }
}