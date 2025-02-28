package ru.test.demo.spl.demo_spl.service;

import lombok.AllArgsConstructor;
import org.springframework.integration.annotation.Transformer;
import org.springframework.stereotype.Service;
import ru.gpn.platform.silverplate.event_collector.cef.lib.model.Event;
import ru.gpn.platform.silverplate.event_collector.cef.lib.model.EventFactory;
import ru.gpn.platform.silverplate.event_collector.cef.lib.model.EventImpl;

import java.util.HashMap;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SyslogService {
    private final EventFactory eventFactory;

    @Transformer(inputChannel = "syslogChannel", outputChannel = "outboundEventChannel")
    private Event transformSyslogToEvent(HashMap<String, Object> message) {

        Event event = new EventImpl();
        if (!message.get("syslog_MESSAGE").toString().isEmpty()) {

            event = eventFactory.fromString(message.get("syslog_MESSAGE").toString());

            event.setId(UUID.randomUUID());
            event.setReceivedAt((new java.util.Date()).getTime());

            event.setMetadataField("facility", message.get("syslog_FACILITY").toString());
            event.setMetadataField("severity", message.get("syslog_SEVERITY").toString());
            event.setMetadataField("version", message.get("syslog_VERSION").toString());
            event.setMetadataField("timestamp", message.get("syslog_TIMESTAMP").toString());
            event.setMetadataField("host", message.get("syslog_HOST").toString());
            event.setMetadataField("tag", message.get("syslog_APP_NAME").toString());
            event.setMetadataField("procid", message.get("syslog_PROCID").toString());
            event.setMetadataField("msgid", message.get("syslog_MSGID").toString());
            event.setMetadataField("message", message.get("syslog_MESSAGE").toString());

        }
        return event;
    }
}
