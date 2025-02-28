package ru.test.demo.spl.demo_spl.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.gpn.platform.silverplate.event_collector.controller.reactive.EventClassIdsApi;
import ru.gpn.platform.silverplate.event_collector.model.EventClassIdV1;
import ru.test.demo.spl.demo_spl.service.EventClassIdService;


import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
public class EventClassIdController implements EventClassIdsApi {
    private final EventClassIdService service;

    @Override
    public Mono<ResponseEntity<Flux<EventClassIdV1>>> getEventClassIds(@Valid String name, @Valid Boolean force, ServerWebExchange serverWebExchange) {
        return Mono.just(ResponseEntity.ok(service.getEventClassIds(name, force)))
            .switchIfEmpty(Mono.error(new NoSuchElementException("event_class_id.error.not_found")));

    }
}
