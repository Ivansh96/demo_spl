package ru.test.demo.spl.demo_spl.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.gpn.platform.silverplate.event_collector.controller.reactive.DeviceVendorsApi;
import ru.gpn.platform.silverplate.event_collector.model.DeviceVendorV1;
import ru.test.demo.spl.demo_spl.service.DeviceVendorService;


import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
public class DeviceVendorController implements DeviceVendorsApi {
    private final DeviceVendorService service;

    @Override
    public Mono<ResponseEntity<Flux<DeviceVendorV1>>> getDeviceVendors(@Valid String name, @Valid Boolean force, ServerWebExchange serverWebExchange) {
        return Mono.just(ResponseEntity.ok(service.getDeviceVendors(name, force)))
            .switchIfEmpty(Mono.error(new NoSuchElementException("device_vendor.error.not_found")));
    }
}
