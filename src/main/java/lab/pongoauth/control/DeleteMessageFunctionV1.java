package lab.pongoauth.control;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import lab.pongoauth.boundary.repository.MessageRepository;
import lab.pongoauth.entity.Message;

public class DeleteMessageFunctionV1 implements DeleteMessageFunction {

  private MessageRepository messageRepository;

  public DeleteMessageFunctionV1(MessageRepository messageRepository) {
    this.messageRepository = messageRepository;
  }

  public void delete(String id, Handler<AsyncResult<Boolean>> asyncResultHandler) {
    this.messageRepository.delete(id, res -> {
      if (res.succeeded()) {
        asyncResultHandler.handle(Future.succeededFuture(res.result()));
      } else {
        asyncResultHandler.handle(Future.failedFuture(res.cause()));
      }
    });
  }
}
