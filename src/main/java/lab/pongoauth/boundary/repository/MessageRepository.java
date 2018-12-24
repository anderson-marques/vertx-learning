package lab.pongoauth.boundary.repository;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import lab.pongoauth.entity.Message;

public interface MessageRepository {

  public void save(final Message message, Handler<AsyncResult<Message>> asyncResultHandler);
  public void listAll(Handler<AsyncResult<List<Message>>> asyncResultHandler);
  public void findById(final String id, Handler<AsyncResult<Message>> asyncResultHandler);
  public void update(final Message message, Handler<AsyncResult<Boolean>> asyncResultHandler);
  public void delete(final String id, Handler<AsyncResult<Boolean>> asyncResultHandler);
  
}