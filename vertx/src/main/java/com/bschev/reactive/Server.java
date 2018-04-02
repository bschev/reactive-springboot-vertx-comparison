package com.bschev.reactive;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.Optional;


public class Server extends AbstractVerticle {

    private DataWebClient dataWebClient = null;

    @Override
    public void start() {
        startHttpServer();
        dataWebClient = new DataWebClient(vertx);
    }

    private void startHttpServer() {
        HttpServerOptions options = new HttpServerOptions().setLogActivity(true);
        HttpServer server = vertx.createHttpServer(options);
        Router router = Router.router(vertx);

        router.route("/resources/*").handler(StaticHandler.create());

        router.get("/data/:id").handler(this::handleDataRequest);

        int port = config().getInteger("server.port");
        server.requestHandler(router::accept).listen(port, res -> {
            if (res.succeeded()) {
                System.out.println("Server is listening on port: " + server.actualPort());
            } else {
                System.out.println("Failed to bind to port: " + port);
            }
        });
    }

    private void handleDataRequest(RoutingContext routingContext) {
        final String idParam = routingContext.request().getParam("id");
        Optional<Long> id = getId(idParam);
        HttpServerResponse response = routingContext.response();
        if (id.isPresent()) {
            dataWebClient.getData(id.get(), data -> {
                if (data == null) {
                    response.setStatusCode(HttpResponseStatus.NOT_FOUND.code()).end();
                } else {
                    response
                            .putHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                            .end(Json.encode(data));
                }
            });
        } else {
            response.setStatusCode(HttpResponseStatus.BAD_REQUEST.code()).end();
        }
    }

    private Optional<Long> getId(String idParam) {
        Optional<Long> id = Optional.empty();
        if (idParam != null) {
            try {
                id = Optional.of(Long.valueOf(idParam));
            } catch (NumberFormatException ignore) {
            }
        }
        return id;
    }
}