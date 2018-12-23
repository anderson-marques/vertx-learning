package lab.pongoauth.boundary.api;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lab.pongoauth.control.FindMessageFunction;
import lab.pongoauth.control.UpdateMessageFunction;
import lab.pongoauth.entity.Message;

public class MessageResource {

  private final FindMessageFunction findMessageService;
  private final UpdateMessageFunction updateMessageService;

  public MessageResource(FindMessageFunction findMessageService, UpdateMessageFunction updateMessageService) {
    this.findMessageService = findMessageService;
    this.updateMessageService = updateMessageService;
  }

  public Handler<RoutingContext> updateMessageHandler() {
    return routingContext -> {
      Message message = Message.createFromJson(routingContext.getBodyAsJson());
      updateMessageService.update(message, res -> {
        if (res.succeeded()){
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
      routingContext.response().end("message updated");
    };
  }

  public Handler<RoutingContext> findMessageHandler() {
    return routingContext -> {
      final String id = routingContext.pathParam("id");

      findMessageService.findMessageById(id, res -> {
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