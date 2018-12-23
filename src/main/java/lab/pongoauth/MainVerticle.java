package lab.pongoauth;

import static lab.pongoauth.boundary.config.EnvironmentValues.MONGO_DB_NAME;
import static lab.pongoauth.boundary.config.EnvironmentValues.WEBAPP_PORT;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQOptions;

import java.util.logging.Level;
import java.util.logging.Logger;

import lab.pongoauth.boundary.api.MessageResource;
import lab.pongoauth.boundary.api.MessagesResource;
import lab.pongoauth.boundary.config.EnvironmentValues;
import lab.pongoauth.boundary.config.RabbitMqConfig;
import lab.pongoauth.boundary.config.WebApplication;
import lab.pongoauth.boundary.repository.MessageRepository;
import lab.pongoauth.boundary.repository.MongoMessageRepository;
import lab.pongoauth.control.FindMessageService;
import lab.pongoauth.control.FindMessageServiceV1;
import lab.pongoauth.control.ListMessagesService;
import lab.pongoauth.control.ListMessagesServiceV1;
import lab.pongoauth.control.SaveMessageService;
import lab.pongoauth.control.SaveMessageServiceV1;

public class MainVerticle extends AbstractVerticle {

  private final EnvironmentValues environmentValues;

  public MainVerticle(final EnvironmentValues environmentValues) {
    this.environmentValues = environmentValues;
  }

  private static final Logger LOGGER = Logger.getLogger(MainVerticle.class.getName());

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    Future<Void> initializationSteps = startMessageBroker()
        .compose(rabbitMQClient -> 
          startMongo().compose(mongoClient -> 
            startWebApplication(mongoClient, rabbitMQClient)));
    initializationSteps.setHandler(startFuture);
  }

  private Future<MongoClient> startMongo() {
    Future<MongoClient> future = Future.future();
    LOGGER.info("Initializing MongoDB...");
    try {
      JsonObject mongoConfig = new JsonObject();
      mongoConfig.put(MONGO_DB_NAME, this.environmentValues.getStringValue(MONGO_DB_NAME));
      MongoClient mongoClient = MongoClient.createNonShared(vertx, mongoConfig);
      LOGGER.info("MongoClient initialized");
      future.complete(mongoClient);
    } catch (Throwable t) {
      LOGGER.log(Level.SEVERE, "Failed to create MongoClient", t);
      future.fail(t);
    }
    return future;
  }

  private Future<Void> startWebApplication(
      final MongoClient mongoClient, 
      final RabbitMQClient rabbitClient
  ) {
    Future<Void> future = Future.future();

    MessageRepository messageRepository = new MongoMessageRepository(mongoClient);
    SaveMessageService saveMessageService = new SaveMessageServiceV1(messageRepository);
    ListMessagesService listMessagesService = new ListMessagesServiceV1(messageRepository);
    FindMessageService findMessageService = new FindMessageServiceV1(messageRepository);
    MessagesResource messagesResource = new MessagesResource(saveMessageService, listMessagesService);
    MessageResource messageResource = new MessageResource(findMessageService);
    Integer port = this.environmentValues.getIntValue(WEBAPP_PORT);

    new WebApplication(vertx, messagesResource, messageResource, port).start(result -> {
      if (result.succeeded()) {
        future.complete();
      } else {
        future.fail(result.cause());
      }
    });
    return future;
  }

  private Future<RabbitMQClient> startMessageBroker() {
    Future<RabbitMQClient> future = Future.future();
    LOGGER.info("Initializing RabbitMQ...");
    try {
      RabbitMQOptions options = new RabbitMqConfig(this.environmentValues).getOptions();
      RabbitMQClient client = RabbitMQClient.create(vertx, options);
      LOGGER.info("RabbitMQ started");
      future.complete(client);
    } catch (Throwable e) {
      LOGGER.info("RabbitMQ failed to start");
      future.fail(e);
    }
    
    return future;
  }
}
