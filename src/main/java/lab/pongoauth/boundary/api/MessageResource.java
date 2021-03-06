package lab.pongoauth.boundary.api;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lab.pongoauth.boundary.events.EventsGateway;
import lab.pongoauth.control.DeleteMessageFunction;
import lab.pongoauth.control.FindMessageFunction;
import lab.pongoauth.control.UpdateMessageFunction;
import lab.pongoauth.entity.Message;

public class MessageResource {

  private final FindMessageFunction findMessageFunction;
  private final UpdateMessageFunction updateMessageFunction;
  private final DeleteMessageFunction deleteMessageFunction;
  private final EventsGateway eventsGateway;

  public MessageResource(
      EventsGateway eventsGateway, 
      FindMessageFunction findMessageService, 
      UpdateMessageFunction updateMessageService, 
      DeleteMessageFunction deleteMessageFunction) {
    this.eventsGateway = eventsGateway;
    this.findMessageFunction = findMessageService;
    this.updateMessageFunction = updateMessageService;
    this.deleteMessageFunction = deleteMessageFunction;
  }

  public Handler<RoutingContext> updateMessageHandler() {
    return routingContext -> {
      Message message = Message.createFromJson(routingContext.getBodyAsJson());
      updateMessageFunction.update(message, res -> {
        if (res.succeeded()){
          eventsGateway.publishEvent("domain", "message_updated", message.toJsonString());
          routingContext.response()
            .putHeader("Content-type", "application/json")
            .end(message.toJsonString());
        } else {
          routingContext.fail(res.cause());
        }
      });
    };
  }

  public Handler<RoutingContext> deleteMessageHandler() {
    return routingContext -> {
      final String id = routingContext.pathParam("id");
      eventsGateway.publishEvent("domain", "message_deleted", "id="+id);
      deleteMessageFunction.delete(id, res -> {
        if (res.succeeded()) {
          routingContext.response().end();
        } else {
          routingContext.fail(res.cause());
        }
      });
    };
  }

  public Handler<RoutingContext> findMessageHandler() {
    return routingContext -> {
      final String id = routingContext.pathParam("id");

      findMessageFunction.findMessageById(id, res -> {
        if (res.succeeded()){
          routingContext.response()
            .putHeader("Content-type", "application/json")
            .end(res.result().toJsonString());
        } else {
          routingContext.fail(res.cause());
        }
      });
    };
  }


}