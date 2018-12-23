package lab.pongoauth.control;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import lab.pongoauth.entity.Message;

@FunctionalInterface
public interface FindMessageFunction {

  public void findMessageById(String id, Handler<AsyncResult<Message>> asyncResultHandler);

}