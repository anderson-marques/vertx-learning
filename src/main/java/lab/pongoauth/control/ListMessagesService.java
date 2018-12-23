package lab.pongoauth.control;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import lab.pongoauth.entity.Message;

public interface ListMessagesService {

  public void listMessages(Handler<AsyncResult<List<Message>>> asyncResultHandler);
  
}