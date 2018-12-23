package lab.pongoauth;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import lab.pongoauth.boundary.config.EnvironmentValues;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

@RunWith(VertxUnitRunner.class)
public class MainVerticleTest {

  private Vertx vertx;

  /**
   * Prepares the tests setting up a test application.
   * 
   * @param context - TextContext
   */
  @Before
  public void setUp(TestContext context) {
    vertx = Vertx.vertx();

    EnvironmentValues env = new EnvironmentValues();

    EnvironmentValues envSpied = Mockito.spy(env);

    Mockito.when(envSpied.getIntValue("WEBAPP_PORT")).thenReturn(7070);

    vertx.deployVerticle(new MainVerticle(envSpied), context.asyncAssertSuccess());
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

    vertx.createHttpClient().getNow(7070, "localhost", "/", responseHandler);
  }
}
