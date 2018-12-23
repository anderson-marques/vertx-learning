package lab.pongoauth.boundary.api;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class MessageResource {

  public MessageResource(){

  }

  private Handler<RoutingContext> updateMessageHandler() {
    return routingContext -> {
      routingContext.response().end("message updated");
    };
  }

  private Handler<RoutingContext> deleteMessageHandler() {
    return routingContext -> {
      routingContext.response().end("message updated");
    };
  }

  private Handler<RoutingContext> findMessageHandler() {
    return routingContext -> {
      routingContext.response().end("message updated");
    };
  }


}