package lab.pongoauth;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQOptions;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MainVerticle extends AbstractVerticle {

  private int port = 8080;

  public MainVerticle setPort(int port) {
    this.port = port;
    return this;
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

  private Future<Void> startWebApplication(final MongoClient mongoClient, final RabbitMQClient rabbitMQClient) {
    Future<Void> future = Future.future();
    LOGGER.info("Initializing Web Application...");
    HttpServer server = vertx.createHttpServer();

    server.requestHandler(request -> {
      // This handler gets called for each request that arrives on the server
      HttpServerResponse response = request.response();
      response.putHeader("content-type", "text/plain");
      // Write to the response and end it
      response.end("pong");
    });

    server.listen(port, result -> {
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
      RabbitMQOptions config = new RabbitMQOptions();
      config.setUri("amqp://guest:guest@localhost:5672");
      RabbitMQClient rabbitMQClient = RabbitMQClient.create(vertx, config);
      LOGGER.info("RabbitMQ started");
      future.complete(rabbitMQClient);
    } catch (Throwable e) {
      LOGGER.info("RabbitMQ failed to start");
      future.fail(e);
    }
    
    return future;
  }
}
