package lab.pongoauth.boundary.config;

import java.util.logging.Logger;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import lab.pongoauth.boundary.api.MessageResource;
import lab.pongoauth.boundary.api.MessagesResource;
import lab.pongoauth.boundary.repository.DocumentConflictException;
import lab.pongoauth.boundary.repository.DocumentNotFoundException;

public class WebApplication {

  private static final String TAG = WebApplication.class.getName();
  private static final Logger LOGGER = Logger.getLogger(TAG);

  private static final String MESSAGES_PATH = "/messages";
  private static final String MESSAGE_PATH = "/messages/:id";

  private final HttpServer server;
  private final Router router;
  private final int port;

  public WebApplication(Vertx vertx, 
                        MessagesResource messagesResource,
                        MessageResource messageResource,
                        int port) {
    LOGGER.info("Initializing Web Application...");

    this.server = vertx.createHttpServer();
    this.router = Router.router(vertx);
    this.port = port;

    Handler<RoutingContext> defaultFailureHandler = failureRoutingContext -> {
      HttpServerResponse response = failureRoutingContext.response();
      Throwable cause = failureRoutingContext.failure();

      if (cause instanceof IllegalArgumentException) {
        response.setStatusCode(400).end();
      } else if (cause instanceof DocumentConflictException) {
        response.setStatusCode(409).end();
      } else if (cause instanceof DocumentNotFoundException) {
        response.setStatusCode(404).end();
      } else {
        response.setStatusCode(500).end();
      }
    };

    router.route()
      .handler(BodyHandler.create())
      .consumes("application/json")
      .produces("application/json");

    router.post(MESSAGES_PATH)
      .handler(messagesResource.createMessageHandler())
      .failureHandler(defaultFailureHandler);

    router.get(MESSAGES_PATH)
      .handler(messagesResource.listMessageHandler())
      .failureHandler(defaultFailureHandler);

    router.get(MESSAGE_PATH)
      .handler(messageResource.findMessageHandler())
      .failureHandler(defaultFailureHandler);

    router.put(MESSAGE_PATH)
      .handler(messageResource.updateMessageHandler())
      .failureHandler(defaultFailureHandler);

    router.delete(MESSAGE_PATH)
      .handler(messageResource.deleteMessageHandler())
      .failureHandler(defaultFailureHandler);

    // Pong resource
    router.get("/ping").handler(res -> {
      res.response().end("pong");
    });

    server.requestHandler(router);
  }

  public void start(Handler<AsyncResult<Void>> resultHandler) {
    this.server.requestHandler(this.router);

    this.server.listen(this.port, result -> {
      if (result.succeeded()) {
        resultHandler.handle(Future.succeededFuture());
      } else {
        resultHandler.handle(Future.failedFuture(result.cause()));
      }
    });
  }
}