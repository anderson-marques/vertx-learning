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
import lab.pongoauth.boundary.events.EventsGateway;
import lab.pongoauth.boundary.repository.MessageRepository;
import lab.pongoauth.boundary.repository.MongoMessageRepository;
import lab.pongoauth.control.DeleteMessageFunction;
import lab.pongoauth.control.DeleteMessageFunctionV1;
import lab.pongoauth.control.FindMessageFunction;
import lab.pongoauth.control.FindMessageFunctionV1;
import lab.pongoauth.control.ListMessagesFunction;
import lab.pongoauth.control.ListMessagesFunctionV1;
import lab.pongoauth.control.SaveMessageFunction;
import lab.pongoauth.control.SaveMessageFunctionV1;
import lab.pongoauth.control.UpdateMessageFunction;
import lab.pongoauth.control.UpdateMessageFunctionV1;

public class MainVerticle extends AbstractVerticle {

  private final EnvironmentValues environmentValues;

  public MainVerticle(final EnvironmentValues environmentValues) {
    this.environmentValues = environmentValues;
  }

  private static final Logger LOGGER = Logger.getLogger(MainVerticle.class.getName());

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    Future<Void> initializationSteps = startMessageBroker()
        .compose(domainEventsGateway -> 
          startMongo().compose(mongoClient -> 
            startWebApplication(mongoClient, domainEventsGateway)));
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
      final EventsGateway domainEventsGateway
  ) {
    Future<Void> future = Future.future();

    MessageRepository messageRepository = new MongoMessageRepository(mongoClient);
    
    SaveMessageFunction saveMessageFunction = new SaveMessageFunctionV1(messageRepository);
    ListMessagesFunction listMessagesFunction = new ListMessagesFunctionV1(messageRepository);
    FindMessageFunction findMessageFunction = new FindMessageFunctionV1(messageRepository);
    UpdateMessageFunction updateMessageFunction = new UpdateMessageFunctionV1(messageRepository);
    DeleteMessageFunction deleteMessageFunction = new DeleteMessageFunctionV1(messageRepository);
    
    MessagesResource messagesResource = new MessagesResource(saveMessageFunction, listMessagesFunction);
    MessageResource messageResource = new MessageResource(findMessageFunction, updateMessageFunction, deleteMessageFunction);

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

  private Future<EventsGateway> startMessageBroker() {
    Future<EventsGateway> future = Future.future();
    LOGGER.info("Initializing RabbitMQ...");
    try {
      RabbitMQOptions options = new RabbitMqConfig(this.environmentValues).getOptions();
      RabbitMQClient client = RabbitMQClient.create(vertx, options);
      EventsGateway domainEventsGateway = new EventsGateway(client);
      domainEventsGateway.start();
      future.complete(domainEventsGateway);
    } catch (Throwable e) {
      LOGGER.info("RabbitMQ failed to start");
      future.fail(e);
    }
    
    return future;
  }
}
