package lab.pongoauth;

import io.vertx.core.Vertx;
import lab.pongoauth.boundary.config.EnvironmentValues;

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

    // Obtaing the EnvironmentValues 
    EnvironmentValues environmentValues = new EnvironmentValues();

    MainVerticle mainVerticle = new MainVerticle(environmentValues);
    // Deploy PingService
    vertx.deployVerticle(mainVerticle);
  }
}