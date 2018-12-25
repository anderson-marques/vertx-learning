package lab.pongoauth.boundary.config;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public interface FailureHandler {

  /**
   * Defines the default failure handler to the routes.
   * @return failureHandler
   */
  public Handler<RoutingContext> handleFailures();

}
