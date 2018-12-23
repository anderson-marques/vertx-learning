package lab.pongoauth.control;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import lab.pongoauth.boundary.repository.MessageRepository;
import lab.pongoauth.entity.Message;

public class FindMessageFunctionV1 implements FindMessageFunction {

  private MessageRepository messageRepository;

  public FindMessageFunctionV1(MessageRepository messageRepository) {
    this.messageRepository = messageRepository;
  }

  @Override
  public void findMessageById(String id, Handler<AsyncResult<Message>> asyncResultHandler) {
    messageRepository.findById(id, res -> {
      if (res.succeeded()){
        asyncResultHandler.handle(Future.succeededFuture(res.result()));
      } else {
        asyncResultHandler.handle(Future.failedFuture(res.cause()));
      }
    });
  }
}