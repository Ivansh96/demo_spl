package ru.test.demo.spl.demo_spl.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import ru.gpn.platform.silverplate.event_collector.model.EventClassIdV1;
import ru.test.demo.spl.demo_spl.entity.EventClassId;
import ru.test.demo.spl.demo_spl.repository.EventClassIdRepository;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class EventClassIdService implements DirectoryService<EventClassId> {
    private final EventClassIdRepository repository;
    private final StorageService storageService;

    @Scheduled(fixedDelay = 10000)
    public void sync() {
        createMissingEntries(new HashSet<>(storageService.getEventClassIds()));
    }

    public Flux<EventClassIdV1> getEventClassIds(final String name) {
        return getEventClassIds(name, false);
    }

    public Flux<EventClassIdV1> getEventClassIds(final String name, boolean forceSync) {
        if (forceSync) {
            sync();
        }

        return Flux.fromStream(
            repository
                .findAllByNameLikeIgnoreCase(String.format("%%%s%%", name != null ? name : "")).stream()
                .map(EventClassId -> new EventClassIdV1()
                    .name(EventClassId.getName())
                ));
    }

    @Override
    public void createMissingEntries(Set<String> currentNames) {
        Set<String> existingNames = new HashSet<>(repository.findAllByNameIn(new HashSet<>(currentNames)).stream().map(EventClassId::getName).toList());
        currentNames.removeIf(existingNames::contains);

        List<EventClassId> newEntries = currentNames
            .stream()
            .map(name -> new EventClassId().setName(name).setDescription(""))
            .toList();

        repository.saveAllAndFlush(newEntries);
    }
}
