package lab.pongoauth.boundary.events;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;

public class EventsGateway {

  private static final String TAG = EventsGateway.class.getName();

  private static final Logger LOGGER = Logger.getLogger(TAG);

  private RabbitMQClient client;

  public EventsGateway(RabbitMQClient client) {
    this.client = client;
  }

  public void publishEvent(String type, String name, String body) {
    
    String exchange = "events-exchange";
    String routingKey = type;

    JsonObject eventMessage = new JsonObject();
    eventMessage.put("type", type);
    eventMessage.put("name", name);
    eventMessage.put("body", body);

	  Handler<AsyncResult<Void>> resultHandler = res -> {
      if (res.succeeded()) {
        LOGGER.info(">>>> Event emitted: " + eventMessage.toString());
      } else {
        LOGGER.warning(">>>> Failed to emit event: " + eventMessage.toString());
      }
    };

	  client.basicPublish(exchange, routingKey, eventMessage, resultHandler);
  }

  public void start(){
    client.start(connResult -> {
      if (connResult.succeeded()){
        declareExchange(exchangeResult -> {
          if (exchangeResult.succeeded()){
            declareQueue(queueResult -> {
              if (queueResult.succeeded()){
                this.publishEvent("operational", "application_started", "Broadcasted event...");
              } else {
                LOGGER.log(Level.WARNING, "Declare queue has failed", queueResult.cause());
              }
            });
          } else {
            LOGGER.log(Level.WARNING, "Declare exchange has failed", exchangeResult.cause());
          }
        });
      } else {
        LOGGER.log(Level.WARNING, "Connection to RabbitMQ has failed", connResult.cause());
      }
    });
  }

  private void declareExchange(Handler<AsyncResult<Void>> asyncResultHandler){
    JsonObject exchangeConfig = new JsonObject();
    exchangeConfig.put("alternate-exchange", "all-events-exchange");
    client.exchangeDeclare(
      "events-exchange", 
      "fanout", true, false, exchangeConfig, res -> {
      if (res.succeeded()) {
        LOGGER.info("Exchange 'domain-events-exchange' declared!");
        asyncResultHandler.handle(Future.succeededFuture());
      } else {
        LOGGER.log(Level.WARNING,"Exchange 'domain-events-exchange' failed to be declared!", res.cause());
        asyncResultHandler.handle(Future.failedFuture(res.cause()));
      }
    });
  }

  private void declareQueue(Handler<AsyncResult<Void>> asyncResultHandler) {
    JsonObject queueConfig = new JsonObject();
    queueConfig.put("x-message-ttl", 10_000L);
    client.queueDeclare("domain-events-queue", true, false, true, queueConfig, res -> {
      if (res.succeeded()) {
        LOGGER.info("Queue 'domain-events-queue' declared!");
        asyncResultHandler.handle(Future.succeededFuture());
      } else {
        LOGGER.info("Queue 'domain-events-queue' failed to be declared!");
        asyncResultHandler.handle(Future.failedFuture(res.cause()));
      }
    });
  }
  
}