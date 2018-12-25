package lab.pongoauth.security;

public class AuthenticationFailedException extends Exception {

  private static final long serialVersionUID = 1L;

  public AuthenticationFailedException(String message) {
    super(message);
  }
}
