package lab.pongoauth;

import static lab.pongoauth.config.EnvironmentValues.APP_TESTING_PORT;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import lab.pongoauth.config.EnvironmentValues;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class MainVerticleTest {

  private Vertx vertx;

  @Before
  public void setUp(TestContext context) {
    vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle(new EnvironmentValues()), context.asyncAssertSuccess());
  }

  @After
  public void tearDown(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

  @Test
  @SuppressWarnings("deprecation")
  public void testMyApplication(TestContext context) {
    final Async async = context.async();

    Handler<HttpClientResponse> responseHandler = response -> {
      response.handler(body -> {
        context.assertTrue(body.toString().contains("pong"));
        async.complete();
      });
    };

    Integer testingPort = new EnvironmentValues().getIntValue(APP_TESTING_PORT);

    vertx.createHttpClient().getNow(testingPort, "localhost", "/", responseHandler);
  }
}
