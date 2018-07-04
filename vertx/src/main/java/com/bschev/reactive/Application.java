package com.bschev.reactive;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class Application {

    public static void main(String args[]) {
        Vertx vertx = Vertx.vertx();
        ConfigRetriever retriever = ConfigRetriever.create(vertx);
        retriever.getConfig(ar -> {
            if (ar.failed()) {
                System.err.println("Failed to retrieve configuration.");
            } else {
                JsonObject config = ar.result();
                DeploymentOptions deploymentOptions = new DeploymentOptions();
                deploymentOptions.setConfig(config);
                vertx.deployVerticle(Server.class, deploymentOptions);
            }
        });
    }

}
