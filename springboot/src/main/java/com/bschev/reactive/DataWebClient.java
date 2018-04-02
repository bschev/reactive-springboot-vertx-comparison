package com.bschev.reactive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class DataWebClient {

    private final WebClient webClient;

    @Autowired
    public DataWebClient(@Value("${server.port}") String port) {
        webClient = WebClient.create("http://localhost:" + port);
    }

    public Mono<Data> getData(Long id) {
        return webClient.get()
                .uri("/resources/data{id}.json", id)
                .exchange()
                .timeout(Duration.ofSeconds(2))
                .flatMap(response -> {
                    if (response.statusCode() == HttpStatus.OK) {
                        return response.bodyToMono(Data.class);
                    } else {
                        return Mono.empty();
                    }
                })
                .doOnError(e -> System.err.println(e.getMessage()))
                .onErrorResume(e -> Mono.empty());
    }
}
