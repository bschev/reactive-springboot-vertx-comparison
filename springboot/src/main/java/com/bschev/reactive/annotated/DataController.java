package com.bschev.reactive.annotated;

import com.bschev.reactive.Data;
import com.bschev.reactive.DataWebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/annotated")
public class DataController {

    private final DataWebClient dataWebClient;

    @Autowired
    public DataController(DataWebClient dataWebClient) {
        this.dataWebClient = dataWebClient;
    }


    @GetMapping("/data/{id}")
    public Mono<ResponseEntity<Data>> handle(@PathVariable Long id) {
        return dataWebClient.getData(id).map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
