package lab.pongoauth.boundary.config;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.logging.Logger;

import lab.pongoauth.boundary.api.MessageResource;
import lab.pongoauth.boundary.api.MessagesResource;
import lab.pongoauth.boundary.api.TokenResource;
import lab.pongoauth.security.Authorizer;

public class WebApplication {

  private static final String TAG = WebApplication.class.getName();
  private static final Logger LOGGER = Logger.getLogger(TAG);

  private static final String MESSAGES_PATH = "/messages";
  private static final String MESSAGE_PATH = "/messages/:id";

  private final HttpServer server;
  private final Router router;
  private final int port;

  /**
   * WebApplication default constructor.
   * 
   * @param vertx - Vert.x instance where the HttpServer is created.
   * @param messagesResource - Messages endpoint handler.
   * @param messageResource - Message endpoing handler.
   * @param tokenResource - Token endpoint handler.
   * @param failureHandler - treats the resource failures.
   * @param authorizer - enforces authorization policies.
   * @param port - HTTP port to serve.
   */
  public WebApplication(
      Vertx vertx, 
      MessagesResource messagesResource, 
      MessageResource messageResource,
      TokenResource tokenResource,
      FailureHandler failureHandler,
      Authorizer authorizer, int port) {
    LOGGER.info("Initializing Web Application...");

    this.server = vertx.createHttpServer();
    this.router = Router.router(vertx);
    this.port = port;

    Handler<RoutingContext> defaultFailureHandler = failureHandler.handleFailures();

    router.route()
    .handler(BodyHandler.create())
    .consumes("application/json")
    .produces("application/json");

    router.post("/tokens")
      .consumes("x-www-form-urlencoded")
      .handler(tokenResource.issueTokenHandler())
      .failureHandler(defaultFailureHandler);

    router.route(MESSAGES_PATH).handler(authorizer.enforceAuthenticated());
    router.route(MESSAGE_PATH).handler(authorizer.enforceAuthenticated());

    router.post(MESSAGES_PATH)
      .handler(authorizer.enforceRoles("admin"))
      .handler(messagesResource.createMessageHandler());

    router.get(MESSAGES_PATH)
      .handler(messagesResource.listMessageHandler());

    router.get(MESSAGE_PATH)
      .handler(messageResource.findMessageHandler());

    router.put(MESSAGE_PATH)
      .handler(authorizer.enforceRoles("admin"))
      .handler(messageResource.updateMessageHandler());

    router.delete(MESSAGE_PATH)
      .handler(authorizer.enforceRoles("admin"))
      .handler(messageResource.deleteMessageHandler());

    router.get("/ping").handler(res -> { 
      res.response().end("pong"); 
    });

    server.requestHandler(router);
  }

  /**
   * Starts the WebApplication.
   * 
   * @param resultHandler - startResultHandler
   */
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
