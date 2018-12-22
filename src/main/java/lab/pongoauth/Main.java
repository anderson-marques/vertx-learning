package lab.pongoauth;

import io.vertx.core.Vertx;

public class Main {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        // Deploy PingService
        vertx.deployVerticle(new MainVerticle());
    }
}