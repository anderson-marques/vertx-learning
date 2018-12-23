package lab.pongoauth;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

@RunWith(VertxUnitRunner.class)
public class MainVerticleTest {

    
    private Vertx vertx;
    private static final int TESTING_PORT = 7070;

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();
        vertx.deployVerticle(
            new MainVerticle().setPort(TESTING_PORT), 
            context.asyncAssertSuccess()
        );
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    @SuppressWarnings( "deprecation" )
    public void testMyApplication(TestContext context) {
        final Async async = context.async();

        Handler<HttpClientResponse> responseHandler = response -> {
            response.handler(body -> {
                context.assertTrue(body.toString().contains("pong"));
                async.complete();
            });
        };
        
        vertx.createHttpClient().getNow(TESTING_PORT, "localhost", "/", responseHandler);
    }
}