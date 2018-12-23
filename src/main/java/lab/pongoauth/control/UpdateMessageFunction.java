package lab.pongoauth.control;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import lab.pongoauth.entity.Message;

@FunctionalInterface
public interface UpdateMessageFunction {

  public void update(Message message, Handler<AsyncResult<Boolean>> asyncResultHandler);
  
}