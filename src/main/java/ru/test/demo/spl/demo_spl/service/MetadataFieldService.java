package ru.test.demo.spl.demo_spl.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import ru.gpn.platform.silverplate.event_collector.model.MetadataFieldV1;
import ru.test.demo.spl.demo_spl.entity.MetadataField;
import ru.test.demo.spl.demo_spl.repository.MetadataFieldRepository;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class MetadataFieldService implements DirectoryService<MetadataField> {
    private final MetadataFieldRepository repository;
    private final StorageService storageService;

    @Scheduled(fixedDelay = 10000)
    public void sync() {
        createMissingEntries(new HashSet<>(storageService.getMetadataFieldNames()));
    }

    public Flux<MetadataFieldV1> getMetadataFields(final String name) {
        return getMetadataFields(name, false);
    }

    public Flux<MetadataFieldV1> getMetadataFields(final String name, boolean forceSync) {
        if (forceSync) {
            sync();
        }

        return Flux.fromStream(
            repository
                .findAllByNameLikeIgnoreCase(String.format("%%%s%%", name != null ? name : "")).stream()
                .map(MetadataField -> new MetadataFieldV1()
                    .name(MetadataField.getName())
                ));
    }

    @Override
    public void createMissingEntries(Set<String> currentNames) {
        Set<String> existingNames = new HashSet<>(repository.findAllByNameIn(new HashSet<>(currentNames)).stream().map(MetadataField::getName).toList());
        currentNames.removeIf(existingNames::contains);

        List<MetadataField> newEntries = currentNames
            .stream()
            .map(name -> new MetadataField().setName(name).setDescription(""))
            .toList();

        repository.saveAllAndFlush(newEntries);
    }
}
