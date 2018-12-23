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
import lab.pongoauth.boundary.api.MessagesResource;
import lab.pongoauth.boundary.repository.DuplicateMessageException;

import static lab.pongoauth.boundary.config.EnvironmentValues.WEBAPP_PORT;

public class WebApplication {

  private static final String TAG = WebApplication.class.getName();
  private static final Logger LOGGER = Logger.getLogger(TAG);
  private static final String MESSAGES_PATH = "/messages";

  private final HttpServer server;
  private final Router router;
  private final EnvironmentValues environmentValues;

  public WebApplication(Vertx vertx, 
                        MessagesResource messagesResource, 
                        EnvironmentValues environmentValues) {
    LOGGER.info("Initializing Web Application...");

    this.server = vertx.createHttpServer();
    this.router = Router.router(vertx);
    this.environmentValues = environmentValues;

    Handler<RoutingContext> defaultFailureHandler = failureRoutingContext -> {
      HttpServerResponse response = failureRoutingContext.response();
      Throwable cause = failureRoutingContext.failure();

      if (cause instanceof IllegalArgumentException) {
        response.setStatusCode(400).end();
      } else if (cause instanceof DuplicateMessageException) {
        response.setStatusCode(409).end();
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

    router.get(MESSAGES_PATH).handler(messagesResource.listMessageHandler());

    // Pong resource
    router.get("/ping").handler(res -> {
      res.response().end("pong");
    });

    server.requestHandler(router);
  }

  public void start(Handler<AsyncResult<Void>> resultHandler) {
    server.requestHandler(router);

    Integer port = this.environmentValues.getIntValue(WEBAPP_PORT);

    this.server.listen(port, result -> {
      if (result.succeeded()) {
        resultHandler.handle(Future.succeededFuture());
      } else {
        resultHandler.handle(Future.failedFuture(result.cause()));
      }
    });
  }
}