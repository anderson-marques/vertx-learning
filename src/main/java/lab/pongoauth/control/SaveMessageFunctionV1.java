package lab.pongoauth.control;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import lab.pongoauth.boundary.repository.MessageRepository;
import lab.pongoauth.entity.Message;

/**
 * SaveMessageService.
 */
public class SaveMessageFunctionV1 implements SaveMessageFunction {

  private final MessageRepository messageRepository;

  public SaveMessageFunctionV1(final MessageRepository messageRepository) {
    this.messageRepository = messageRepository;
  }

  @Override
  public void saveMessage(final Message message, Handler<AsyncResult<Message>> resultHandler) {
    this.messageRepository.save(message, saveResult -> {
      if (saveResult.succeeded()){
        resultHandler.handle(Future.succeededFuture(saveResult.result()));
      } else {
        resultHandler.handle(Future.failedFuture(saveResult.cause()));
      }
    });
  }
}