package lab.pongoauth.control;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.jwt.JWTAuth;

public class AuthenticationFunctionV1 implements AuthenticationFunction {

  private AuthProvider authProvider;
  private JWTAuth jwtProvider;

  public AuthenticationFunctionV1(AuthProvider authProvider, JWTAuth jwtProvider) {
    this.authProvider = authProvider;
    this.jwtProvider = jwtProvider;
  }

	@Override
	public void authenticate(String username, String password, Handler<AsyncResult<JsonObject>> asyncResultHandler) {
    JsonObject authInfo = new JsonObject();
    authInfo.put("username", username);
    authInfo.put("password", password);
		this.authProvider.authenticate(authInfo, authenticationResult -> {
      if (authenticationResult.succeeded()) {
        User authenticatedUser = authenticationResult.result();

        long secondsToExpire = Duration.ofMinutes(5).getSeconds();
        long issuedAt = Instant.now().getEpochSecond();
        long expirationTime =  Instant.now().plusSeconds(secondsToExpire).getEpochSecond();

        JsonObject claims = new JsonObject();
        
        claims.put("iss", "this");
        claims.put("sub", username);
        claims.put("aud", "this");
        claims.put("exp", expirationTime);
        claims.put("iat", issuedAt);
        claims.put("jti", UUID.randomUUID().toString());
        claims.put("roles", authenticatedUser.principal().getJsonArray("roles"));
        claims.put("scopes", "read write");
        
        String accessToken = jwtProvider.generateToken(claims);

        JsonObject authenticationResponse = new JsonObject();

        authenticationResponse.put("access_token", accessToken);
        authenticationResponse.put("expires_in", secondsToExpire);
        authenticationResponse.put("scopes", "read write");

        asyncResultHandler.handle(Future.succeededFuture(authenticationResponse));
      } else {
        JsonObject errorResponse = new JsonObject();

        errorResponse.put("error", "invalid_client");
        errorResponse.put("error_description","Client authentication failed");

        asyncResultHandler.handle(Future.succeededFuture(errorResponse));
      }
    });
	}
}
