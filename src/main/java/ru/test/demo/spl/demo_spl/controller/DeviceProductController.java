package ru.test.demo.spl.demo_spl.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.gpn.platform.silverplate.event_collector.controller.reactive.DeviceProductsApi;
import ru.gpn.platform.silverplate.event_collector.model.DeviceProductV1;
import ru.test.demo.spl.demo_spl.service.DeviceProductService;


import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
public class DeviceProductController implements DeviceProductsApi {
    private final DeviceProductService service;

    @Override
    public Mono<ResponseEntity<Flux<DeviceProductV1>>> getDeviceProducts(@Valid String name, @Valid Boolean force, ServerWebExchange serverWebExchange) {
        return Mono.just(ResponseEntity.ok(service.getDeviceProducts(name, force)))
            .switchIfEmpty(Mono.error(new NoSuchElementException("device_product.error.not_found")));
    }
}
