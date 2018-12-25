package lab.pongoauth.security;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;

public interface Authenticator {

  public void authenticate(JsonObject credentials, Handler<AsyncResult<User>> asyncResultHandler);

}