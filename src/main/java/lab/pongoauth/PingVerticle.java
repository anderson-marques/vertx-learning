package lab.pongoauth;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.vertx.core.AbstractVerticle;

public class PingVerticle extends AbstractVerticle {

  private static final Logger LOGGER = Logger.getLogger(PingVerticle.class.getName());

  @Override
  public void start() throws Exception {
    vertx.createHttpServer().requestHandler(r -> {
      r.response().end("pong");
    }).listen(8000, result -> {
      if (result.succeeded()) {
        LOGGER.info("PingVerticle started");
      } else {
        LOGGER.log(Level.SEVERE, "PingVerticle failed to start", result.cause());
      }
    });
  }
}
