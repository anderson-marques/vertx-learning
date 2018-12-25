package lab.pongoauth;

import static lab.pongoauth.boundary.config.EnvironmentValues.ADMIN_PASSWORD;
import static lab.pongoauth.boundary.config.EnvironmentValues.MONGO_DB_NAME;
import static lab.pongoauth.boundary.config.EnvironmentValues.WEBAPP_PORT;
import static lab.pongoauth.boundary.config.EnvironmentValues.JWT_TOKEN_SECRET;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.auth.mongo.HashAlgorithm;
import io.vertx.ext.auth.mongo.MongoAuth;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import lab.pongoauth.boundary.api.MessageResource;
import lab.pongoauth.boundary.api.MessagesResource;
import lab.pongoauth.boundary.api.TokenResource;
import lab.pongoauth.boundary.config.DefaultFailureHandler;
import lab.pongoauth.boundary.config.EnvironmentValues;
import lab.pongoauth.boundary.config.FailureHandler;
import lab.pongoauth.boundary.config.RabbitMqConfig;
import lab.pongoauth.boundary.config.WebApplication;
import lab.pongoauth.boundary.events.EventsGateway;
import lab.pongoauth.boundary.repository.MessageRepository;
import lab.pongoauth.boundary.repository.MongoMessageRepository;
import lab.pongoauth.control.DeleteMessageFunction;
import lab.pongoauth.control.DeleteMessageFunctionV1;
import lab.pongoauth.control.FindMessageFunction;
import lab.pongoauth.control.FindMessageFunctionV1;
import lab.pongoauth.control.ListMessagesFunction;
import lab.pongoauth.control.ListMessagesFunctionV1;
import lab.pongoauth.control.SaveMessageFunction;
import lab.pongoauth.control.SaveMessageFunctionV1;
import lab.pongoauth.control.UpdateMessageFunction;
import lab.pongoauth.control.UpdateMessageFunctionV1;
import lab.pongoauth.security.Authenticator;
import lab.pongoauth.security.Authorizer;
import lab.pongoauth.security.JwtAuthorizer;
import lab.pongoauth.security.JwtTokenGenerator;
import lab.pongoauth.security.SecurityTokenGenerator;
import lab.pongoauth.security.UserCredentialsAuthenticator;

public class MainVerticle extends AbstractVerticle {

  private final EnvironmentValues environmentValues;
  private MongoAuth mongoAuthProvider;
  private MongoClient mongoClient;
  private EventsGateway eventsGateway;

  public MainVerticle(final EnvironmentValues environmentValues) {
    this.environmentValues = environmentValues;
  }

  private static final Logger LOGGER = Logger.getLogger(MainVerticle.class.getName());

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    Future<Void> initializationSteps = 
      startMessageBroker().compose(resStartMessageBroker -> 
        startMongo().compose( resStartMongo -> 
          startWebApplication(this.mongoClient, this.mongoAuthProvider, this.eventsGateway)
        )
      );
    initializationSteps.setHandler(startFuture);
  }

  private Future<Void> startMongo() {
    Future<Void> future = Future.future();
    LOGGER.info("Initializing MongoDB...");
    try {
      JsonObject mongoConfig = new JsonObject();
      mongoConfig.put(MONGO_DB_NAME, this.environmentValues.getStringValue(MONGO_DB_NAME));
      this.mongoClient = MongoClient.createNonShared(vertx, mongoConfig);

      JsonObject authProperties = new JsonObject();
      this.mongoAuthProvider = MongoAuth.create(mongoClient, authProperties);
      mongoAuthProvider.setHashAlgorithm(HashAlgorithm.PBKDF2);

      mongoClient.createIndex("user", new JsonObject()
        .put("username",1)
        .put("unique", true)
        , res -> {
        if(res.succeeded()){
          LOGGER.info("username index created in user collection!");

          // Try to find the admin user. If it does not exists, create it.
          mongoClient.findOne("user", new JsonObject().put("username","admin"), null, findAdminRes -> {
            if(findAdminRes.succeeded() && findAdminRes.result() == null){
              List<String> roles = new ArrayList<>();
              roles.add("admin");
              List<String> permissions = new ArrayList<>();
              String password = this.environmentValues.getStringValue(ADMIN_PASSWORD);
              mongoAuthProvider.insertUser("admin", password, roles, permissions, saveRes -> {
                if(saveRes.succeeded()){
                  LOGGER.info("admin user created in user collection!");
                } else {
                  LOGGER.log(Level.WARNING, "Failed to create admin user!", saveRes.cause());
                }
              });
            }
          });
        } else {
          LOGGER.log(Level.WARNING, "Failed to create username index in user collection!", res.cause());
        }
      });
      
      LOGGER.info("MongoClient initialized");
      future.complete();
    } catch (Throwable t) {
      LOGGER.log(Level.SEVERE, "Failed to create MongoClient", t);
      future.fail(t);
    }
    return future;
  }

  private Future<Void> startWebApplication(
      final MongoClient mongoClient, 
      final MongoAuth mongoAuth,
      final EventsGateway eventsGateway
  ) {
    Future<Void> future = Future.future();

    MessageRepository messageRepository = new MongoMessageRepository(mongoClient);
    
    SaveMessageFunction saveMessageFunction = new SaveMessageFunctionV1(messageRepository);
    ListMessagesFunction listMessagesFunction = new ListMessagesFunctionV1(messageRepository);
    FindMessageFunction findMessageFunction = new FindMessageFunctionV1(messageRepository);
    UpdateMessageFunction updateMessageFunction = new UpdateMessageFunctionV1(messageRepository);
    DeleteMessageFunction deleteMessageFunction = new DeleteMessageFunctionV1(messageRepository);

    JWTAuth jwtAuth = JWTAuth.create(vertx, new JWTAuthOptions()
      .addPubSecKey(new PubSecKeyOptions()
        .setAlgorithm("HS256")
        .setPublicKey(this.environmentValues.getStringValue(JWT_TOKEN_SECRET))
        .setSymmetric(true)));
    
    MessagesResource messagesResource = new MessagesResource(eventsGateway, saveMessageFunction, listMessagesFunction);
    MessageResource messageResource = new MessageResource(eventsGateway, findMessageFunction, updateMessageFunction, deleteMessageFunction);
    SecurityTokenGenerator securityTokenGenerator = new JwtTokenGenerator(environmentValues, jwtAuth);
    Authenticator authenticator = new UserCredentialsAuthenticator(this.mongoAuthProvider);
    TokenResource tokenResource = new TokenResource(authenticator, environmentValues, securityTokenGenerator);
    FailureHandler failureHandler = new DefaultFailureHandler();

    Authorizer authorizer = new JwtAuthorizer(jwtAuth);

    Integer port = this.environmentValues.getIntValue(WEBAPP_PORT);

    WebApplication app = new WebApplication(vertx, messagesResource, messageResource, tokenResource, failureHandler, authorizer, port);

    app.start(result -> {
      if (result.succeeded()) {
        future.complete();
      } else {
        future.fail(result.cause());
      }
    });
    return future;
  }

  private Future<Void> startMessageBroker() {
    Future<Void> future = Future.future();
    LOGGER.info("Initializing RabbitMQ...");
    try {
      RabbitMQOptions options = new RabbitMqConfig(this.environmentValues).getOptions();
      RabbitMQClient client = RabbitMQClient.create(vertx, options);
      this.eventsGateway = new EventsGateway(client);
      this.eventsGateway.start();
      future.complete();
    } catch (Throwable e) {
      LOGGER.info("RabbitMQ failed to start");
      future.fail(e);
    }
    
    return future;
  }
}
