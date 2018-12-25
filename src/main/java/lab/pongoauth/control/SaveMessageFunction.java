package lab.pongoauth.control;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import lab.pongoauth.entity.Message;

@FunctionalInterface
public interface SaveMessageFunction {
  public void saveMessage(final Message message, Handler<AsyncResult<Message>> resultHandler);
}