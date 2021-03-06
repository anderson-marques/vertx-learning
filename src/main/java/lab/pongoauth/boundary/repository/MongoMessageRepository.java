package lab.pongoauth.boundary.repository;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.MongoWriteException;

import lab.pongoauth.entity.Message;

public class MongoMessageRepository implements MessageRepository {

  private final MongoClient mongoClient;

  private final static String MESSAGES_COLLECTION = "messages";

  public MongoMessageRepository(final MongoClient mongoClient) {
    this.mongoClient = mongoClient;
  }

  @Override
  public void save(final Message message, final Handler<AsyncResult<Message>> asyncResultHandler) {
    JsonObject document = message.toJson();

    if (message.getId() != null) {
      document.put("_id", message.getId());
    }

    mongoClient.insert(MESSAGES_COLLECTION, document, res -> {
      if (res.succeeded()) {
        String autoGeneratedId = res.result();
        if (autoGeneratedId != null ){
          document.put("id",autoGeneratedId);
        }
        asyncResultHandler.handle(Future.succeededFuture(Message.createFromJson(document)));
      } else {
        if (res.cause() instanceof MongoWriteException && res.cause().getMessage().contains("E11000 duplicate key error collection")){
          DocumentConflictException exception = new DocumentConflictException("Already exists a message with the provided id.",res.cause());
          asyncResultHandler.handle(Future.failedFuture(exception));
        } else {
          asyncResultHandler.handle(Future.failedFuture(res.cause()));
        }
      }
    });
  }

  @Override
  public void listAll(Handler<AsyncResult<List<Message>>> asyncResultHandler) {
    JsonObject query = new JsonObject();
    List<Message> messages = new ArrayList<>();
    mongoClient.find(MESSAGES_COLLECTION, query, res -> {
      if (res.succeeded()) {
        for (JsonObject json : res.result()) {
          messages.add(Message.createFromJsonString(json.toString()));
        }
        asyncResultHandler.handle(Future.succeededFuture(messages));
      } else {
        asyncResultHandler.handle(Future.failedFuture(res.cause()));
      }
    });
  }

  @Override
  public void findById(String id, Handler<AsyncResult<Message>> asyncResultHandler) {
    JsonObject query = new JsonObject();
    query.put("_id", id);
    mongoClient.findOne(MESSAGES_COLLECTION, query, null, res -> {
      if (res.succeeded()) {
        if (res.result() == null) {
          DocumentNotFoundException cause = new DocumentNotFoundException("message");
          asyncResultHandler.handle(Future.failedFuture(cause));
        } else {
          asyncResultHandler.handle(Future.succeededFuture(Message.createFromJson(res.result())));
        }
      } else {
        asyncResultHandler.handle(Future.failedFuture(res.cause()));
      }
    });
  }

  @Override
  public void update(Message message, Handler<AsyncResult<Boolean>> asyncResultHandler) {
    JsonObject document = message.toJson();
    document.put("_id", message.getId());

    mongoClient.save(MESSAGES_COLLECTION, document, res -> {
      if (res.succeeded()) {
        asyncResultHandler.handle(Future.succeededFuture(true));
      } else {
        asyncResultHandler.handle(Future.failedFuture(res.cause()));
      }
    });
  }

  @Override
  public void delete(String id, Handler<AsyncResult<Boolean>> asyncResultHandler) {
    JsonObject query = new JsonObject();
    query.put("_id", id);
    mongoClient.removeDocument(MESSAGES_COLLECTION, query, res -> {
      if (res.succeeded()) {
        asyncResultHandler.handle(Future.succeededFuture(true));
      } else {
        asyncResultHandler.handle(Future.failedFuture(res.cause()));
      }
    });
  }
}
