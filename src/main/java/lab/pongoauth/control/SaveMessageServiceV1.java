package lab.pongoauth.control;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import lab.pongoauth.boundary.repository.MessageRepository;
import lab.pongoauth.entity.Message;

/**
 * SaveMessageService.
 */
public class SaveMessageServiceV1 implements SaveMessageService {

  private final MessageRepository messageRepository;

  public SaveMessageServiceV1(final MessageRepository messageRepository) {
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

  @Override
  public void listMessages(Handler<AsyncResult<List<Message>>> resultHandler) {
    throw new UnsupportedOperationException("Not implemented yet");
  }
}