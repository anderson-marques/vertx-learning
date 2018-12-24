package lab.pongoauth.boundary.api;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lab.pongoauth.control.AuthenticationFunction;

public class AuthenticationResource {

  AuthenticationFunction authenticationFunction;

  public AuthenticationResource(AuthenticationFunction authenticationFunction) {
    this.authenticationFunction = authenticationFunction;
  }

  public Handler<RoutingContext> authenticateUser() {
    return routingContext -> {
      routingContext.request().setExpectMultipart(true);
      routingContext.request().endHandler(res -> {
        String username = routingContext.request().getFormAttribute("username");
        String password = routingContext.request().getFormAttribute("password");

        this.authenticationFunction.authenticate(username, password, authenticationResult -> {
          if (authenticationResult.succeeded()) {
            JsonObject authenticationResponse = authenticationResult.result();
            routingContext.response().setStatusCode(200).putHeader("Content-Type", "application/json")
                .end(authenticationResponse.toString());
          } else {
            JsonObject errorResponse = authenticationResult.result();
            routingContext.response().setStatusCode(401).putHeader("WWW-Authenticate", "Basic")
                .putHeader("Content-Type", "application/json").end(errorResponse.toString());
          }
        });
      });
    };
  }
}
