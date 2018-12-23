package lab.pongoauth.boundary.repository;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.logging.Level;
import java.util.logging.Logger;

import lab.pongoauth.entity.Message;

public class MongoMessageRepository implements MessageRepository {

  private static final String TAG = MongoMessageRepository.class.getName();
  private static final Logger LOGGER = Logger.getLogger(TAG);

  private final MongoClient mongoClient;

  public MongoMessageRepository(final MongoClient mongoClient) {
    this.mongoClient = mongoClient;
  }

  @Override
  public Future<String> save(final Message message) {
    Future<String> future = Future.future();
    future.complete("some-id");

    JsonObject document = new JsonObject();
    if (message.getId() != null) {
      document.put("_id", message.getId());
    }
    document.put("text", message.getText());

    mongoClient.save("messages", document, res -> {
      if (res.succeeded()) {
        String id = res.result();
        LOGGER.info("Saved message with id " + id);
      } else {
        LOGGER.log(Level.WARNING, "Failed to save message: " + document, res.cause());
      }
    });

    return future;
  }

}