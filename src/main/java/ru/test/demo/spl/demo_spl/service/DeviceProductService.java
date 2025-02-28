package ru.test.demo.spl.demo_spl.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import ru.gpn.platform.silverplate.event_collector.model.DeviceProductV1;
import ru.test.demo.spl.demo_spl.entity.DeviceProduct;
import ru.test.demo.spl.demo_spl.repository.DeviceProductRepository;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class DeviceProductService implements DirectoryService<DeviceProduct> {
    private final DeviceProductRepository repository;
    private final StorageService storageService;

    @Scheduled(fixedDelay = 10000)
    public void sync() {
        createMissingEntries(new HashSet<>(storageService.getDeviceProductNames()));
    }

    public Flux<DeviceProductV1> getDeviceProducts(final String name) {
        return getDeviceProducts(name, false);
    }

    public Flux<DeviceProductV1> getDeviceProducts(final String name, boolean forceSync) {
        if (forceSync) {
            sync();
        }

        return Flux.fromStream(
            repository
                .findAllByNameLikeIgnoreCase(String.format("%%%s%%", name != null ? name : "")).stream()
                .map(deviceProduct -> new DeviceProductV1()
                    .name(deviceProduct.getName())
                ));
    }

    @Override
    public void createMissingEntries(Set<String> currentNames) {
        Set<String> existingNames = new HashSet<>(repository.findAllByNameIn(new HashSet<>(currentNames)).stream().map(DeviceProduct::getName).toList());
        currentNames.removeIf(existingNames::contains);

        List<DeviceProduct> newEntries = currentNames
            .stream()
            .map(name -> new DeviceProduct().setName(name).setDescription(""))
            .toList();

        repository.saveAllAndFlush(newEntries);
    }
}
