package lab.pongoauth.boundary.config;

import static lab.pongoauth.boundary.config.EnvironmentValues.RABBITMQ_HOST;
import static lab.pongoauth.boundary.config.EnvironmentValues.RABBITMQ_PASSWORD;
import static lab.pongoauth.boundary.config.EnvironmentValues.RABBITMQ_PORT;
import static lab.pongoauth.boundary.config.EnvironmentValues.RABBITMQ_USER;

import io.vertx.rabbitmq.RabbitMQOptions;

public class RabbitMqConfig {

  private RabbitMQOptions options;

  public RabbitMqConfig(final EnvironmentValues environmentValues) {
    this.options = defineOptions(environmentValues);
  }

  public RabbitMQOptions getOptions() {
    return this.options;
  }

  private RabbitMQOptions defineOptions(final EnvironmentValues env) {
    RabbitMQOptions options = new RabbitMQOptions();
    options.setUser(env.getStringValue(RABBITMQ_USER));
    options.setPassword(env.getStringValue(RABBITMQ_PASSWORD));
    options.setHost(env.getStringValue(RABBITMQ_HOST));
    options.setPort(env.getIntValue(RABBITMQ_PORT));
    return options;
  }
}
