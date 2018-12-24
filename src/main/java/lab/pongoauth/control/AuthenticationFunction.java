package lab.pongoauth.control;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

public interface AuthenticationFunction {

  public void authenticate(String username, String password, Handler<AsyncResult<JsonObject>> asyncResultHandler);
}