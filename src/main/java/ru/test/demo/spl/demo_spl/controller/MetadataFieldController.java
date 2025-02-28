package ru.test.demo.spl.demo_spl.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.gpn.platform.silverplate.event_collector.controller.reactive.MetadataFieldsApi;
import ru.gpn.platform.silverplate.event_collector.model.MetadataFieldV1;
import ru.test.demo.spl.demo_spl.service.MetadataFieldService;


import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
public class MetadataFieldController implements MetadataFieldsApi {
    private final MetadataFieldService service;

    @Override
    public Mono<ResponseEntity<Flux<MetadataFieldV1>>> getMetadataFields(@Valid String name, @Valid Boolean force, ServerWebExchange serverWebExchange) {
        return Mono.just(ResponseEntity.ok(service.getMetadataFields(name, force)))
            .switchIfEmpty(Mono.error(new NoSuchElementException("metadata_field.error.not_found")));
    }
}
