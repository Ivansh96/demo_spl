package ru.test.demo.spl.demo_spl.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import ru.gpn.platform.silverplate.event_collector.model.ExtensionFieldV1;
import ru.test.demo.spl.demo_spl.entity.ExtensionField;
import ru.test.demo.spl.demo_spl.repository.ExtensionFieldRepository;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class ExtensionFieldService implements DirectoryService<ExtensionField> {
    private final ExtensionFieldRepository repository;
    private final StorageService storageService;

    @Scheduled(fixedDelay = 20000)
    public void sync() {
        List<String> x = storageService.getExtensionFieldNames();
        createMissingEntries(new HashSet<>(storageService.getExtensionFieldNames()));
    }

    public Flux<ExtensionFieldV1> getExtensionFields(final String name) {
        return getExtensionFields(name, false);
    }

    public Flux<ExtensionFieldV1> getExtensionFields(final String name, boolean forceSync) {
        if (forceSync) {
            sync();
        }

        return Flux.fromStream(
            repository
                .findAllByNameLikeIgnoreCase(String.format("%%%s%%", name != null ? name : "")).stream()
                .map(ExtensionField -> new ExtensionFieldV1()
                    .name(ExtensionField.getName())
                ));
    }

    @Override
    public void createMissingEntries(Set<String> currentNames) {
        Set<String> existingNames = new HashSet<>(repository.findAllByNameIn(new HashSet<>(currentNames)).stream().map(ExtensionField::getName).toList());
        currentNames.removeIf(existingNames::contains);

        List<ExtensionField> newEntries = currentNames
            .stream()
            .map(name -> new ExtensionField().setName(name).setDescription(""))
            .toList();

        repository.saveAllAndFlush(newEntries);
    }
}
