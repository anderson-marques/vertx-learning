package lab.pongoauth.boundary.api;

import io.vertx.core.Handler;
import io.vertx.core.json.EncodeException;
import io.vertx.ext.web.RoutingContext;
import lab.pongoauth.control.SaveMessageService;
import lab.pongoauth.entity.Message;

public class MessagesResource {

  private final SaveMessageService saveMessageService;

  public MessagesResource(final SaveMessageService saveMessageService){
    this.saveMessageService = saveMessageService;
  }

  public Handler<RoutingContext> createMessageHandler() {
    return routingContext -> {
      try {
        final String stringBody = routingContext.getBodyAsString();
        final Message newMessage = Message.createFromJsonString(stringBody);

        this.saveMessageService.saveMessage(newMessage, res -> {
          if (res.succeeded()){
            routingContext.response().setStatusCode(201);
            routingContext.response().end(res.result().toJson().toString());
          } else {
            routingContext.fail(res.cause());
          }
        });
      } catch (IllegalArgumentException | EncodeException e) {
        routingContext.fail(e);
      }
    };
  }

  public Handler<RoutingContext> listMessageHandler() {
    return routingContext -> {
      routingContext.response().end("listing messages...");
    };
  }

}