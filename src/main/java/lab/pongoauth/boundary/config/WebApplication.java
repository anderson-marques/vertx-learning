package lab.pongoauth.boundary.config;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import lab.pongoauth.boundary.api.MessagesResource;

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

    Handler<RoutingContext> defaultFailureHandler = routingContext -> {
      if (routingContext.failure() instanceof IllegalArgumentException) {
        routingContext.response().setStatusCode(400);
        routingContext.response().end();
      }
    };

    ;
    router.route()
      .handler(BodyHandler.create())
      .consumes("application/json")
      .produces("application/json");

    router.exceptionHandler(throwable -> {
      LOGGER.log(Level.WARNING, "Fail to execute the service", throwable);
    });

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