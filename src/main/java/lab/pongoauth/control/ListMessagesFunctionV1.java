package lab.pongoauth.control;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import lab.pongoauth.boundary.repository.MessageRepository;
import lab.pongoauth.entity.Message;

public class ListMessagesFunctionV1 implements ListMessagesFunction {

  private MessageRepository repository;

  public ListMessagesFunctionV1(MessageRepository repository) {
    this.repository = repository;
  }

  public void listMessages(Handler<AsyncResult<List<Message>>> asyncResultHandler) {

    repository.listAll(listResult -> {
      if (listResult.succeeded()){
        asyncResultHandler.handle(Future.succeededFuture(listResult.result()));
      } else {
        asyncResultHandler.handle(Future.failedFuture(listResult.cause()));
      }
    });
  }
}