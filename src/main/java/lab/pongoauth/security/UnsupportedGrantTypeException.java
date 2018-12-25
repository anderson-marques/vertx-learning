package lab.pongoauth.security;

public class UnsupportedGrantTypeException extends Exception {

  private static final long serialVersionUID = 1L;

  public UnsupportedGrantTypeException() {
    super();
  }

  public UnsupportedGrantTypeException(String message) {
    super(message);
  }
  
}
