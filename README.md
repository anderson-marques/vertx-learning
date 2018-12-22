# Vert.x

The purpose of this repo is to provide a running application that:

- Serves a HTTP endpoint
- Saves/Query the request body from a NoSQL database (MongoDB)
- Emits domain events using RabbitMQ
- Authenticates the user using JWT
- Restrict access to `/config` endpoint only to users with `admin` role
- Restricts the endpoints access only to authenticated users using Google OIDC

## Testing the application

Always that a new dependency or source file change is required to rebuild the test image:

```bash
docker-compose build test
```

The test image caches all the dependencies. It allow to run the build command
before every test without any overhead or additional network usage.

```bash
docker-compose build test && docker-compose run test
```

## Running the application

The application can be started using the `docker-compose up` command:

```bash
docker-compose build app && docker-compose up -d app
```

The `-d` argument starts the application in background mode, i.e. without attaching the current terminal. You can ommit that argument to tail the application logs.

You can tail the application logs when the application is running in background mode executing the following command:

```bash
$ docker-compose logs -f app

Attaching to vertx-learning_app_1
app_1   | Dec 22, 2018 10:17:33 PM io.vertx.core.Starter
app_1   | INFO: Succeeded in deploying verticle
app_1   | Dec 22, 2018 10:17:33 PM lab.pongoauth.MainVerticle
app_1   | INFO: MainVerticle started

```