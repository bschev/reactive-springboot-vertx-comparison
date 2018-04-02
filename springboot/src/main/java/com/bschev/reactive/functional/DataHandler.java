package com.bschev.reactive.functional;

import com.bschev.reactive.Data;
import com.bschev.reactive.DataWebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

@Component
public class DataHandler {

    private final DataWebClient dataWebClient;

    @Autowired
    public DataHandler(DataWebClient dataWebClient) {
        this.dataWebClient = dataWebClient;
    }

    Mono<ServerResponse> data(ServerRequest request) {

        Optional<Long> id = getId(request.pathVariable("id"));
        if (id.isPresent()) {
            Mono<Data> dataMono = dataWebClient.getData(id.get());
            return dataMono
                    .flatMap(data ->
                            ServerResponse.ok().contentType(APPLICATION_JSON_UTF8).body(BodyInserters.fromObject(data)))
                    .switchIfEmpty(ServerResponse.notFound().build());
        } else {
            return ServerResponse.badRequest().build();
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
