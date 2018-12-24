package lab.pongoauth.control;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@FunctionalInterface
public interface DeleteMessageFunction {

  public void delete(String id, Handler<AsyncResult<Boolean>> asyncResultHandler);
  
}