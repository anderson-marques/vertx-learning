package lab.pongoauth.security;

import io.vertx.ext.auth.User;

public interface SecurityTokenGenerator {

  public String generate(final User user, final long secondsToExpire);
  
}
