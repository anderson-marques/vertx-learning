package lab.pongoauth.boundary.api;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lab.pongoauth.control.FindMessageService;
import lab.pongoauth.entity.Message;

public class MessageResource {

  private FindMessageService findMessageService;

  public MessageResource(FindMessageService findMessageService) {
    this.findMessageService = findMessageService;
  }

  public Handler<RoutingContext> updateMessageHandler() {
    return routingContext -> {
      routingContext.response().end("message updated");
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