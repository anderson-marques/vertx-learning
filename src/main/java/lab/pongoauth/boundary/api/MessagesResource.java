package lab.pongoauth.boundary.api;

import io.vertx.core.Handler;
import io.vertx.core.json.EncodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import lab.pongoauth.control.ListMessagesFunction;
import lab.pongoauth.control.SaveMessageFunction;
import lab.pongoauth.entity.Message;

public class MessagesResource {

  private final SaveMessageFunction saveMessageService;
  private final ListMessagesFunction listMessagesService;

  public MessagesResource(final SaveMessageFunction saveMessageService, final ListMessagesFunction listMessagesService){
    this.saveMessageService = saveMessageService;
    this.listMessagesService = listMessagesService;
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
      try {
        this.listMessagesService.listMessages(listResult -> {
          if(listResult.succeeded()){
            routingContext
              .response()
              .putHeader("Content-Type", "application/json")
              .end(new JsonArray(listResult.result()).toString());
          } else {
            routingContext.fail(listResult.cause());
          }
        });
      } catch (Exception e) {
        routingContext.fail(e);
      }
    };
  }

}