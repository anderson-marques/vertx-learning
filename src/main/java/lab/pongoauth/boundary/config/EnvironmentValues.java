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

  public static final String ACCESS_TOKEN_SECRET = "ACCESS_TOKEN_SECRET";

  private static final String DEFAULT_WEBAPP_PORT = "8080";
  private static final String DEFAULT_RABBITMQ_USER = "guest";
  private static final String DEFAULT_RABBITMQ_PASSWORD = "guest";
  private static final String DEFAULT_RABBITMQ_HOST = "localhost";
  private static final String DEFAULT_RABBITMQ_PORT = "5672";

  private static final String DEFAULT_MONGO_DB_NAME = "messages_db";

  private static final String DEFAULT_ADMIN_PASSWORD = "admin";

  private static final String DEFAULT_ACCESS_TOKEN_SECRET = 
    "access-token-secret-access-token-secret-access-token-secret-access-token-secret-access-token-secret";

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
    values.put(ACCESS_TOKEN_SECRET, System.getenv(ACCESS_TOKEN_SECRET) == null 
        ? DEFAULT_ACCESS_TOKEN_SECRET : System.getenv(ACCESS_TOKEN_SECRET));
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
}
