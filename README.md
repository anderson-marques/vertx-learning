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

## Environment Variables

Environment variables required by the application:

- `APP_TESTING_PORT` - Web Application Port used in tests. Default: `7070`.
- `WEB_API_PORT` - Web Application Port. Default: `8080`.
- `RABBITMQ_USER` - RabbitMQ username. Default: `guest`.
- `RABBITMQ_PASSWORD` - RabbitMQ username. Default: `guest`.
- `RABBITMQ_HOST` - RabbitMQ hostname. Default: `localhost`.
- `RABBITMQ_PORT` - RabbitMQ port. Default: `5672`.

## Troubleshooting

### Macbook Pro Users

If you use a mackbook Pro. The DSN resolution for localhost can present some delay. To fix this problem check your hostname and include localhost resolution
in the `/etc/hosts` file:

```bash
$ hostname
MacBook-Pro-de-Anderson.local

$ vim /etc/hosts
127.0.0.1       localhost
255.255.255.255 broadcasthost
::1             localhost
## Include this lines
127.0.0.1   MacBook-Pro-de-Anderson.local
::1         MacBook-Pro-de-Anderson.local
```