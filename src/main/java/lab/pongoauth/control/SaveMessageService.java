package lab.pongoauth.control;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import lab.pongoauth.entity.Message;

/**
 * SaveMessageService.
 */
public interface SaveMessageService {

  public void saveMessage(final Message message, Handler<AsyncResult<Message>> resultHandler);
  public void listMessages(Handler<AsyncResult<List<Message>>> resultHandler);
}