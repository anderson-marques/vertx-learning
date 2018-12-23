package lab.pongoauth.control;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import lab.pongoauth.boundary.repository.MessageRepository;
import lab.pongoauth.entity.Message;

public class UpdateMessageFunctionV1 implements UpdateMessageFunction {

  private MessageRepository messageRepository;

  public UpdateMessageFunctionV1(MessageRepository messageRepository) {
    this.messageRepository = messageRepository;
  }

  public void update(Message message, Handler<AsyncResult<Boolean>> asyncResultHandler) {
    this.messageRepository.update(message, res -> {
      if (res.succeeded() && res.result()) {
        asyncResultHandler.handle(Future.succeededFuture(true));
      } else {
        asyncResultHandler.handle(Future.failedFuture(res.cause()));
      }
    });
  }
}
