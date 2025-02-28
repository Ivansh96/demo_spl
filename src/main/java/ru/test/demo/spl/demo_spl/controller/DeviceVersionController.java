package ru.test.demo.spl.demo_spl.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.gpn.platform.silverplate.event_collector.controller.reactive.DeviceVersionsApi;
import ru.gpn.platform.silverplate.event_collector.model.DeviceVersionV1;
import ru.test.demo.spl.demo_spl.service.DeviceVersionService;


import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
public class DeviceVersionController implements DeviceVersionsApi {
    private final DeviceVersionService service;

    @Override
    public Mono<ResponseEntity<Flux<DeviceVersionV1>>> getDeviceVersions(@Valid String name, @Valid Boolean force, ServerWebExchange serverWebExchange) {
        return Mono.just(ResponseEntity.ok(service.getDeviceVersions(name, force)))
            .switchIfEmpty(Mono.error(new NoSuchElementException("device_version.error.not_found")));
    }
}
