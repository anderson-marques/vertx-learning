package lab.pongoauth.config;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentValues {

  private Map<String, String> values = new HashMap<String, String>();

  public static final String APP_TESTING_PORT = "APP_TESTING_PORT";
  public static final String RABBITMQ_USER = "RABBITMQ_USER";
  public static final String RABBITMQ_PASSWORD = "RABBITMQ_PASSWORD";
  public static final String RABBITMQ_HOST = "RABBITMQ_HOST";
  public static final String RABBITMQ_PORT = "RABBITMQ_PORT";

  private static final String DEFAULT_APP_TESTING_PORT = "7070";
  private static final String DEFAULT_RABBITMQ_USER = "guest";
  private static final String DEFAULT_RABBITMQ_PASSWORD = "guest";
  private static final String DEFAULT_RABBITMQ_HOST = "localhost";
  private static final String DEFAULT_RABBITMQ_PORT = "5672";

  public EnvironmentValues() {
    values.put(RABBITMQ_USER, System.getenv(RABBITMQ_USER) == null 
      ? DEFAULT_RABBITMQ_USER : System.getenv(RABBITMQ_USER));
    values.put(RABBITMQ_PASSWORD, System.getenv(RABBITMQ_PASSWORD) == null 
      ? DEFAULT_RABBITMQ_PASSWORD : System.getenv(RABBITMQ_PASSWORD));
    values.put(RABBITMQ_HOST, System.getenv(RABBITMQ_HOST) == null 
      ? DEFAULT_RABBITMQ_HOST : System.getenv(RABBITMQ_PORT));
    values.put(RABBITMQ_PORT, System.getenv(RABBITMQ_HOST) == null 
      ? DEFAULT_RABBITMQ_PORT : System.getenv(RABBITMQ_PORT));
    values.put(APP_TESTING_PORT, System.getenv(APP_TESTING_PORT) == null 
      ? DEFAULT_APP_TESTING_PORT : System.getenv(APP_TESTING_PORT));
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

  public Integer getIntValue(final String key){
    try {
      return Integer.parseInt(this.getStringValue(key));
    } catch(Exception e) {
      return null; // EnvironmentVariable was not set
    }
  }
}
