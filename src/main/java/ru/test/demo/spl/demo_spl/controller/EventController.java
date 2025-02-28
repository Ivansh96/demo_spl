package ru.test.demo.spl.demo_spl.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.gpn.platform.silverplate.event_collector.controller.reactive.EventsApi;
import ru.gpn.platform.silverplate.event_collector.model.EventV1;
import ru.test.demo.spl.demo_spl.service.EventService;


import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventController implements EventsApi {
    private final EventService service;

    @Override
    public Mono<ResponseEntity<Flux<EventV1>>> getEvents(@Valid List<String> extensionFields, @Valid List<String> metadataFields, @Valid List<Integer> versions, @Valid List<String> deviceVendors, @Valid List<String> deviceProducts, @Valid List<String> deviceVersions, @Valid List<String> eventClassIds, @Min(0L) @Valid Integer offset, @Max(100000L) @Valid Integer limit, ServerWebExchange serverWebExchange) {
        return Mono.just(ResponseEntity.ok(service.getEvents(extensionFields, metadataFields, versions, deviceVendors, deviceProducts, deviceVersions, eventClassIds, offset, limit)));
    }
}
