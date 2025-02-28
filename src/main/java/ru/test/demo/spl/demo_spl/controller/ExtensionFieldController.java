package ru.test.demo.spl.demo_spl.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.gpn.platform.silverplate.event_collector.controller.reactive.ExtensionFieldsApi;
import ru.gpn.platform.silverplate.event_collector.model.ExtensionFieldV1;
import ru.test.demo.spl.demo_spl.service.ExtensionFieldService;


import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
public class ExtensionFieldController implements ExtensionFieldsApi {
    private final ExtensionFieldService service;

    @Override
    public Mono<ResponseEntity<Flux<ExtensionFieldV1>>> getExtensionFields(@Valid String name, @Valid Boolean force, ServerWebExchange serverWebExchange) {
        return Mono.just(ResponseEntity.ok(service.getExtensionFields(name, force)))
            .switchIfEmpty(Mono.error(new NoSuchElementException("extension_field.error.not_found")));
    }
}
