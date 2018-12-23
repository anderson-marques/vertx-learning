package lab.pongoauth.boundary.repository;

import io.vertx.core.Future;
import lab.pongoauth.entity.Message;

public interface MessageRepository {

  public Future<String> save(final Message message);
}