package lab.pongoauth.boundary.api;

import io.vertx.core.Handler;
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
	    this.saveMessageService.saveMessage(new Message("new-id").setText("some message"), res -> {
        routingContext.response().end("message saved");
      });
    };
  }

  public Handler<RoutingContext> listMessageHandler() {
    return routingContext -> {
      routingContext.response().end("listing messages...");
    };
  }

}