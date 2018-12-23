package lab.pongoauth;



import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQOptions;

import java.util.logging.Level;
import java.util.logging.Logger;

import lab.pongoauth.boundary.api.MessagesResource;
import lab.pongoauth.boundary.config.EnvironmentValues;
import lab.pongoauth.boundary.config.RabbitMqConfig;
import lab.pongoauth.boundary.config.WebApplication;
import lab.pongoauth.boundary.repository.MessageRepository;
import lab.pongoauth.boundary.repository.MongoMessageRepository;
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
      MongoClient mongoClient = MongoClient.createNonShared(vertx, new JsonObject());
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
    MessagesResource messagesController = new MessagesResource(saveMessageService);

    WebApplication webApplication = new WebApplication(vertx, messagesController, this.environmentValues);
    webApplication.start(result -> {
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
