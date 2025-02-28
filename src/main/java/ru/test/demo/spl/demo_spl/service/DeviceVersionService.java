package ru.test.demo.spl.demo_spl.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import ru.gpn.platform.silverplate.event_collector.model.DeviceVersionV1;
import ru.test.demo.spl.demo_spl.entity.DeviceVersion;
import ru.test.demo.spl.demo_spl.repository.DeviceVersionRepository;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class DeviceVersionService implements DirectoryService<DeviceVersion> {
    private final DeviceVersionRepository repository;
    private final StorageService storageService;

    @Scheduled(fixedDelay = 10000)
    public void sync() {
        createMissingEntries(new HashSet<>(storageService.getDeviceVersions()));
    }

    public Flux<DeviceVersionV1> getDeviceVersions(final String name) {
        return getDeviceVersions(name, false);
    }

    public Flux<DeviceVersionV1> getDeviceVersions(final String name, boolean forceSync) {
        if (forceSync) {
            sync();
        }

        return Flux.fromStream(
            repository
                .findAllByNameLikeIgnoreCase(String.format("%%%s%%", name != null ? name : "")).stream()
                .map(DeviceVersion -> new DeviceVersionV1()
                    .name(DeviceVersion.getName())
                ));
    }

    @Override
    public void createMissingEntries(Set<String> currentNames) {
        Set<String> existingNames = new HashSet<>(repository.findAllByNameIn(new HashSet<>(currentNames)).stream().map(DeviceVersion::getName).toList());
        currentNames.removeIf(existingNames::contains);

        List<DeviceVersion> newEntries = currentNames
            .stream()
            .map(name -> new DeviceVersion().setName(name).setDescription(""))
            .toList();

        repository.saveAllAndFlush(newEntries);
    }
}
