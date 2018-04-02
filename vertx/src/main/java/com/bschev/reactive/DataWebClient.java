package com.bschev.reactive;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;

public class DataWebClient {

    private HttpClient httpClient;

    public DataWebClient(Vertx vertx) {
        HttpClientOptions options = new HttpClientOptions().setLogActivity(true);
        options.setDefaultHost("localhost");
        options.setDefaultPort(vertx.getOrCreateContext().config().getInteger("server.port"));
        httpClient = vertx.createHttpClient(options);
    }

    public void getData(Long id, Handler<Data> resultHandler) {
        httpClient.request(HttpMethod.GET, "/resources/data" + id + ".json", response -> {
            if (response.statusCode() == HttpResponseStatus.OK.code()) {
                response.bodyHandler(body -> {
                    JsonObject jsonObject = body.toJsonObject();
                    Data data = jsonObject.mapTo(Data.class);
                    resultHandler.handle(data);
                });
            } else {
                resultHandler.handle(null);
            }
            response.exceptionHandler(ex -> {
                System.err.println(ex.getMessage());
                //resultHandler.handle(null);
            });
        }).exceptionHandler(ex -> {
            System.err.println(ex.getMessage());
            resultHandler.handle(null);
        }).setTimeout(2000).end();
    }

}
