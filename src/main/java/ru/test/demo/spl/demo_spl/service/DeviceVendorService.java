package ru.test.demo.spl.demo_spl.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import ru.gpn.platform.silverplate.event_collector.model.DeviceVendorV1;
import ru.test.demo.spl.demo_spl.entity.DeviceVendor;
import ru.test.demo.spl.demo_spl.repository.DeviceVendorRepository;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class DeviceVendorService implements DirectoryService<DeviceVendor> {
    private final DeviceVendorRepository repository;
    private final StorageService storageService;

    @Scheduled(fixedDelay = 10000)
    public void sync() {
        createMissingEntries(new HashSet<>(storageService.getDeviceVendorNames()));
    }

    public Flux<DeviceVendorV1> getDeviceVendors(final String name) {
        return getDeviceVendors(name, false);
    }

    public Flux<DeviceVendorV1> getDeviceVendors(final String name, boolean forceSync) {
        if (forceSync) {
            sync();
        }

        return Flux.fromStream(
            repository
                .findAllByNameLikeIgnoreCase(String.format("%%%s%%", name != null ? name : "")).stream()
                .map(deviceVendor -> new DeviceVendorV1()
                    .name(deviceVendor.getName())
                ));
    }

    @Override
    public void createMissingEntries(Set<String> currentNames) {
        Set<String> existingNames = new HashSet<>(repository.findAllByNameIn(new HashSet<>(currentNames)).stream().map(DeviceVendor::getName).toList());
        currentNames.removeIf(existingNames::contains);

        List<DeviceVendor> newEntries = currentNames
            .stream()
            .map(name -> new DeviceVendor().setName(name).setDescription(""))
            .toList();

        repository.saveAllAndFlush(newEntries);
    }
}
