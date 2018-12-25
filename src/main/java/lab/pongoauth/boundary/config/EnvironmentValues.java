package lab.pongoauth.boundary.config;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentValues {

  private Map<String, String> values = new HashMap<String, String>();

  public static final String WEBAPP_PORT = "WEBAPP_PORT";
  
  public static final String RABBITMQ_USER = "RABBITMQ_USER";
  public static final String RABBITMQ_PASSWORD = "RABBITMQ_PASSWORD";
  public static final String RABBITMQ_HOST = "RABBITMQ_HOST";
  public static final String RABBITMQ_PORT = "RABBITMQ_PORT";

  public static final String MONGO_DB_NAME = "MONGO_DB_NAME";

  public static final String ADMIN_PASSWORD = "ADMIN_PASSWORD";

  public static final String JWT_TOKEN_SECRET = "JWT_TOKEN_SECRET";
  public static final String JWT_TOKEN_AUDIENCE = "JWT_TOKEN_AUDIENCE";
  public static final String JWT_TOKEN_ISSUER = "JWT_TOKEN_ISSUER";
  public static final String JWT_TOKEN_LIFETIME_IN_SECONDS = "JWT_TOKEN_LIFETIME_IN_SECONDS";

  private static final String DEFAULT_WEBAPP_PORT = "8080";
  private static final String DEFAULT_RABBITMQ_USER = "guest";
  private static final String DEFAULT_RABBITMQ_PASSWORD = "guest";
  private static final String DEFAULT_RABBITMQ_HOST = "localhost";
  private static final String DEFAULT_RABBITMQ_PORT = "5672";

  private static final String DEFAULT_MONGO_DB_NAME = "messages_db";

  private static final String DEFAULT_ADMIN_PASSWORD = "admin";

  private static final String DEFAULT_JWT_TOKEN_SECRET = "WzqRd46wpCjJFGuunuGGfxqveo6zCCR1fw8MczQv";
  private static final String DEFAULT_JWT_TOKEN_AUDIENCE = "this.service.com";
  private static final String DEFAULT_JWT_TOKEN_ISSUER = "this.service.com";
  private static final String DEFAULT_JWT_TOKEN_LIFETIME_IN_SECONDS = "300";

  /**
   * Creates a new EnvironmentValues object pre-setted with default values.
   */
  public EnvironmentValues() {
    values.put(RABBITMQ_USER, System.getenv(RABBITMQ_USER) == null 
        ? DEFAULT_RABBITMQ_USER : System.getenv(RABBITMQ_USER));
    values.put(RABBITMQ_PASSWORD, System.getenv(RABBITMQ_PASSWORD) == null 
        ? DEFAULT_RABBITMQ_PASSWORD : System.getenv(RABBITMQ_PASSWORD));
    values.put(RABBITMQ_HOST, System.getenv(RABBITMQ_HOST) == null 
        ? DEFAULT_RABBITMQ_HOST : System.getenv(RABBITMQ_PORT));
    values.put(RABBITMQ_PORT, System.getenv(RABBITMQ_HOST) == null 
        ? DEFAULT_RABBITMQ_PORT : System.getenv(RABBITMQ_PORT));
    values.put(WEBAPP_PORT, System.getenv(WEBAPP_PORT) == null 
        ? DEFAULT_WEBAPP_PORT : System.getenv(WEBAPP_PORT));
    values.put(MONGO_DB_NAME, System.getenv(MONGO_DB_NAME) == null 
        ? DEFAULT_MONGO_DB_NAME : System.getenv(MONGO_DB_NAME));
    values.put(ADMIN_PASSWORD, System.getenv(ADMIN_PASSWORD) == null 
        ? DEFAULT_ADMIN_PASSWORD : System.getenv(ADMIN_PASSWORD));
    values.put(JWT_TOKEN_SECRET, System.getenv(JWT_TOKEN_SECRET) == null 
        ? DEFAULT_JWT_TOKEN_SECRET : System.getenv(JWT_TOKEN_SECRET));
    values.put(JWT_TOKEN_AUDIENCE, System.getenv(JWT_TOKEN_AUDIENCE) == null 
        ? DEFAULT_JWT_TOKEN_AUDIENCE : System.getenv(JWT_TOKEN_AUDIENCE));
    values.put(JWT_TOKEN_ISSUER, System.getenv(JWT_TOKEN_ISSUER) == null 
        ? DEFAULT_JWT_TOKEN_ISSUER : System.getenv(JWT_TOKEN_ISSUER));
    values.put(JWT_TOKEN_LIFETIME_IN_SECONDS, System.getenv(JWT_TOKEN_LIFETIME_IN_SECONDS) == null 
        ? DEFAULT_JWT_TOKEN_LIFETIME_IN_SECONDS : System.getenv(JWT_TOKEN_LIFETIME_IN_SECONDS));
  }

  /**
   * Returns the value of an environment variable. 
   * 
   * @param key - Environment variable name
   * @return value - Environment variable value in string format
   */
  public String getStringValue(final String key) {
    return this.values.get(key);
  }

  /**
   * Returns the value of an environment variable as an Integer. 
   * @param key - Environment variable name
   * @return value - Environment variable value in Integer format
   */
  public Integer getIntValue(final String key) {
    try {
      return Integer.parseInt(this.getStringValue(key));
    } catch (Exception e) {
      return null; // EnvironmentVariable was not set
    }
  }

  /**
   * Returns the value of an environment variable as a Long. 
   * @param key - Environment variable name
   * @return value - Environment variable value in Long format
   */
  public Long getLongValue(final String key) {
    try {
      return Long.parseLong(this.getStringValue(key));
    } catch (Exception e) {
      return null; // EnvironmentVariable was not set
    }
  }
}
