package lab.pongoauth;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.vertx.core.AbstractVerticle;

public class MainVerticle extends AbstractVerticle {

  private int port = 8080;

  public MainVerticle setPort(int port){
    this.port = port;
    return this;
  }

  private static final Logger LOGGER = Logger.getLogger(MainVerticle.class.getName());

  @Override
  public void start() throws Exception {
    vertx.createHttpServer().requestHandler(r -> {
      r.response().end("pong");
    }).listen(port, result -> {
      if (result.succeeded()) {
        LOGGER.info("MainVerticle started");
      } else {
        LOGGER.log(Level.SEVERE, "MainVerticle failed to start", result.cause());
      }
    });
  }
}
