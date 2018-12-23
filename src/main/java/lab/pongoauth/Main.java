package lab.pongoauth;

import io.vertx.core.Vertx;

public class Main {

  public static void main(String[] args) {
    new Main().start();
  }

  /**
   * Starts the application deploying the MainVerticle.
   */
  public void start() {
    // Create the Verticle
    Vertx vertx = Vertx.vertx();
    MainVerticle mainVerticle = new MainVerticle();
    // Deploy PingService
    vertx.deployVerticle(mainVerticle);
  }
}