package lab.pongoauth.boundary.api;

import static lab.pongoauth.boundary.config.EnvironmentValues.JWT_TOKEN_LIFETIME_IN_SECONDS;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RoutingContext;

import java.util.logging.Level;
import java.util.logging.Logger;

import lab.pongoauth.boundary.config.EnvironmentValues;
import lab.pongoauth.security.Authenticator;
import lab.pongoauth.security.InvalidRequestException;
import lab.pongoauth.security.SecurityTokenGenerator;
import lab.pongoauth.security.UnsupportedGrantTypeException;

public class TokenResource {

  private static final String ACCESS_TOKEN = "access_token";
  private static final String EXPIRES_IN = "expires_in";
  private static final String GRANT_TYPE = "grant_type";
  private static final String PASSWORD = "password";
  private static final String USERNAME = "username";

  private static final String TAG = TokenResource.class.getName();
  private static final Logger LOGGER = Logger.getLogger(TAG);

  private final Authenticator authenticator;
  private final EnvironmentValues environmentValues;
  private final SecurityTokenGenerator securityTokenGenerator;

  /**
   * TokenResource is the endpoint responsible for issue access tokens used by this application.
   * 
   * @param authenticator - Authenticator responsible to ensure that the credentials are correct.
   * @param environmentValues - EnvironmentValues that holds app configuration values.
   * @param securityTokenGenerator - Generates the access token.
   */
  public TokenResource(final Authenticator authenticator, final EnvironmentValues environmentValues,
      final SecurityTokenGenerator securityTokenGenerator) {
    this.authenticator = authenticator;
    this.environmentValues = environmentValues;
    this.securityTokenGenerator = securityTokenGenerator;
  }

  /**
   * Issue access tokens according to RFC 6749, OAuth 2.0 Resource Owner Password Credentials.
   * 
   * <p>The authorization server is also the resource server. The scope and client authentication
   * are dismissed.
   * 
   * @return tokenResponse - token response in JsonObject format
   */
  public Handler<RoutingContext> issueTokenHandler() {
    return routingContext -> {
      HttpServerRequest request = routingContext.request();
      HttpServerResponse response = routingContext.response();
      request.setExpectMultipart(true);
      request.endHandler(res -> {
        
        String grantType = request.getFormAttribute(GRANT_TYPE);
        String username = request.getFormAttribute(USERNAME);
        String password = request.getFormAttribute(PASSWORD);

        try {
          validateGrantType(grantType);
          validateCredentials(username, password);

          JsonObject credentials = new JsonObject();
          credentials.put(USERNAME, username);
          credentials.put(PASSWORD, password);

          this.authenticator.authenticate(credentials, authenticationResult -> {
            if (authenticationResult.succeeded()) {
              User user = authenticationResult.result();
              long lifetimeInSeconds = 
                  environmentValues.getLongValue(JWT_TOKEN_LIFETIME_IN_SECONDS);
              String accessToken = this.securityTokenGenerator.generate(user, lifetimeInSeconds);

              JsonObject accessTokenResponse = new JsonObject();
              accessTokenResponse.put(ACCESS_TOKEN, accessToken);
              accessTokenResponse.put(EXPIRES_IN, lifetimeInSeconds);

              response.setStatusCode(200).putHeader("Content-Type", "application/json")
                  .end(accessTokenResponse.toString());
            } else {
              final String errorDescription = "Invalid username or password.";
              LOGGER.log(Level.WARNING, errorDescription, authenticationResult.cause());
              JsonObject errorResponse = new JsonObject();
              errorResponse.put("error", "invalid_grant");
              errorResponse.put("error_description", errorDescription);
              response.setStatusCode(400)
                .putHeader("Content-Type", "application/json")
                .end(errorResponse.toString());
            }
          });
        } catch (InvalidRequestException e) {
          JsonObject errorResponse = new JsonObject();
          errorResponse.put("error", "invalid_request");
          errorResponse.put("error_description", e.getMessage());
          response.setStatusCode(400)
            .putHeader("Content-Type", "application/json")
            .end(errorResponse.toString());
        } catch (UnsupportedGrantTypeException e) {
          JsonObject errorResponse = new JsonObject();
          errorResponse.put("error", "unsupported_grant_type");
          errorResponse.put("error_description", e.getMessage());
          response.setStatusCode(400)
            .putHeader("Content-Type", "application/json")
            .end(errorResponse.toString());
        }
      });
    };
  }

  private void validateGrantType(
      final String grantType) throws InvalidRequestException, UnsupportedGrantTypeException {
    if (grantType == null) {
      throw new InvalidRequestException("Parameter grant_type is required.");
    } else if (!grantType.equals("password")) {
      throw new UnsupportedGrantTypeException("Grant type not supported");
    }
  }

  private void validateCredentials(
      final String username,
      final String password) throws InvalidRequestException {
    if (username == null || password == null) {
      throw new InvalidRequestException("Invalid credentials");
    }
  }
}
