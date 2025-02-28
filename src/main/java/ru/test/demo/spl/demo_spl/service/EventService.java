package ru.test.demo.spl.demo_spl.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.gpn.platform.silverplate.event_collector.model.EventV1;
import ru.gpn.platform.silverplate.event_collector.model.EventV1Common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EventService {
    private final StorageService storageService;

    public Flux<EventV1> getEvents(List<String> extensionFields, List<String> metadataFields, List<Integer> versions, List<String> deviceVendors, List<String> deviceProducts, List<String> deviceVersions, List<String> eventClassIds, Integer page, Integer limit) {
        return Flux.fromIterable(
            storageService
                .getEventResult(
                    versions,
                    deviceVendors,
                    deviceProducts,
                    deviceVersions,
                    eventClassIds,
                    page,
                    limit)
                .map(record -> {
                    EventV1 event = new EventV1();

                    // Enrich basic
                    event.setId(record.get("b_id", String.class));
                    event.setReceivedAt(record.get("b_receivedAt", Long.class));
                    event.setProcessedAt(record.get("b_processedAt", Long.class));

                    // Enrich common
                    EventV1Common common = new EventV1Common();

                    common.deviceVendor(record.get("c_deviceVendor", String.class));
                    common.deviceProduct(record.get("c_deviceProduct", String.class));
                    common.deviceVersion(record.get("c_deviceVersion", String.class));
                    common.eventClassId(record.get("c_eventClassId", String.class));
                    common.name(record.get("c_name", String.class));
                    common.severity(record.get("c_severity", Integer.class));

                    event.setCommon(common);

                    // Enrich extension
                    Map<String, Object> extension = new HashMap<>();

                    extensionFields
                        .forEach(name -> extension.put(name, record.get(String.format("e_%s", name), String.class)));

                    event.setExtensions(extension);

                    // Enrich metadata
                    Map<String, Object> metadata = new HashMap<>();

                    metadataFields
                        .forEach(name -> metadata.put(name, record.get(String.format("m_%s", name), String.class)));

                    event.setMetadata(metadata);

                    return event;
                })
        );
    }

}
