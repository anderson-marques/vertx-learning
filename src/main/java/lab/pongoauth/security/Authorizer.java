package lab.pongoauth.security;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public interface Authorizer {

  public Handler<RoutingContext> enforceAuthenticated();
  public Handler<RoutingContext> enforceRoles(String... allowedRoles);

}