package lab.pongoauth.boundary.repository;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import lab.pongoauth.entity.Message;

public interface MessageRepository {

  public void save(final Message message, Handler<AsyncResult<Message>> asyncResultHandler);
}