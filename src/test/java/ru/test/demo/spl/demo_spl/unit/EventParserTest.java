package ru.test.demo.spl.demo_spl.unit;

import org.junit.jupiter.api.Test;
import ru.gpn.platform.silverplate.event_collector.cef.lib.model.Event;
import ru.gpn.platform.silverplate.event_collector.cef.lib.model.EventFactoryImpl;
import ru.gpn.platform.silverplate.event_collector.cef.lib.model.EventImpl;
import ru.gpn.platform.silverplate.event_collector.cef.lib.model.ExtensionParserImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static ru.test.demo.spl.demo_spl.util.LogUtil.CEF1;
import static ru.test.demo.spl.demo_spl.util.LogUtil.SPL1;

public class EventParserTest {

    EventFactoryImpl eventFactory = new EventFactoryImpl();

    @Test
    void testExtensionParser() {
        String extensions = "field0=value_without_spaces0 field1=value_in_quotes_without_spaces1 field2=value in quotes with spaces2 field3=other value in quotes with spaces3 field4=value not in quotes with spaces 4 field5=other value not in quotes with spaces 5";

        ExtensionParserImpl parser = new ExtensionParserImpl();

        Map<String, String> parsedExtension = parser.parse(extensions);
        Map<String, String> expectedExtension = new HashMap<>();

        expectedExtension.put("field0", "value_without_spaces0");
        expectedExtension.put("field1", "value_in_quotes_without_spaces1");
        expectedExtension.put("field2", "value in quotes with spaces2");
        expectedExtension.put("field3", "other value in quotes with spaces3");
        expectedExtension.put("field4", "value not in quotes with spaces 4");
        expectedExtension.put("field5", "other value not in quotes with spaces 5");

        assert expectedExtension.equals(parsedExtension);
    }
    @Test
    void testCefPrefixParser() {
        String prefix = "CEF";
        Map<String, Event> eventMap = new HashMap<>();

        eventMap.put(
                "CEF:0|vendor|product|version|signature|name|0|field0=value_without_spaces0 field1=value_in_quotes_without_spaces1 field2=value in quotes with spaces2 field3=other value in quotes with spaces3 field4=value not in quotes with spaces 4 field5=other value not in quotes with spaces 5",
                (new EventImpl())
                        .setVersion(0)
                        .setDeviceVendor("vendor")
                        .setDeviceProduct("product")
                        .setDeviceVersion("version")
                        .setEventClassId("signature")
                        .setName("name")
                        .setSeverity((short) 0)
                        .setExtension("field0", "value_without_spaces0")
                        .setExtension("field1", "value_in_quotes_without_spaces1")
                        .setExtension("field2", "value in quotes with spaces2")
                        .setExtension("field3", "other value in quotes with spaces3")
                        .setExtension("field4", "value not in quotes with spaces 4")
                        .setExtension("field5", "other value not in quotes with spaces 5")
        );

        eventMap.put(
                "CEF:0|fil-it.ru|segment-transfer-ms-main|1.6.0-SNAPSHOT|login error|KeycloakAccessToken|3|src=10.129.10.58 dst=0.0.0.0",
                (new EventImpl())
                        .setVersion(0)
                        .setDeviceVendor("fil-it.ru")
                        .setDeviceProduct("segment-transfer-ms-main")
                        .setDeviceVersion("1.6.0-SNAPSHOT")
                        .setEventClassId("login error")
                        .setName("KeycloakAccessToken")
                        .setSeverity((short) 3)
                        .setExtension("src", "10.129.10.58")
                        .setExtension("dst", "0.0.0.0")
        );

        eventMap.put("CEF:0|security|threatmanager|1.0|100|worm successfully stopped|10|src=10.0.0.1 dst=2.1.2.2 spt=1232",
                (new EventImpl())
                        .setVersion(0)
                        .setDeviceVendor("security")
                        .setDeviceProduct("threatmanager")
                        .setDeviceVersion("1.0")
                        .setEventClassId("100")
                        .setName("worm successfully stopped")
                        .setSeverity((short) 10)
                        .setExtension("src", "10.0.0.1")
                        .setExtension("dst", "2.1.2.2")
                        .setExtension("spt", "1232")
        );


        eventMap.forEach((message, event) -> {
            Event parsedEvent = eventFactory.fromString(message, prefix);
            assert (Objects.equals(parsedEvent.getVersion(), event.getVersion()));
            assert (Objects.equals(parsedEvent.getDeviceVendor(), event.getDeviceVendor()));
            assert (Objects.equals(parsedEvent.getDeviceProduct(), event.getDeviceProduct()));
            assert (Objects.equals(parsedEvent.getDeviceVersion(), event.getDeviceVersion()));
            assert (Objects.equals(parsedEvent.getEventClassId(), event.getEventClassId()));
            assert (Objects.equals(parsedEvent.getName(), event.getName()));
            assert (Objects.equals(parsedEvent.getSeverity(), event.getSeverity()));
            assert (event.getExtensions().equals(parsedEvent.getExtensions()));
        });
    }

    @Test
    void testSplPrefixParser() {
        String prefix = "SPL";

        Event testEvent = new EventImpl()
                .setVersion(1)
                .setDeviceVendor("fil-it.ru")
                .setDeviceProduct("file-transfer")
                .setDeviceVersion("1.9.3-SNAPSHOT")
                .setEventClassId("100")
                .setName("create")
                .setSeverity((short) 3)
                .setExtension("suser", "test.SE@testmail.ru")
                .setExtension("src", "10.129.4.37")
                .setExtension("dhost", "test-dhost-d88594")
                .setExtension("shost", "test-shost-12932js");

        Event parsedFromStringEvent = eventFactory.fromString(SPL1, prefix);

        assert (Objects.equals(testEvent.getVersion(), parsedFromStringEvent.getVersion()));
        assert (Objects.equals(testEvent.getDeviceVendor(), parsedFromStringEvent.getDeviceVendor()));
        assert (Objects.equals(testEvent.getDeviceProduct(), parsedFromStringEvent.getDeviceProduct()));
        assert (Objects.equals(testEvent.getDeviceVersion(), parsedFromStringEvent.getDeviceVersion()));
        assert (Objects.equals(testEvent.getEventClassId(), parsedFromStringEvent.getEventClassId()));
        assert (Objects.equals(testEvent.getName(), parsedFromStringEvent.getName()));
        assert (Objects.equals(testEvent.getSeverity(), parsedFromStringEvent.getSeverity()));
        assert (testEvent.getExtensions().equals(parsedFromStringEvent.getExtensions()));

    }

    @Test
    void testToString() {
        Event event = new EventImpl()
                .setVersion(1)
                .setDeviceVendor("security")
                .setDeviceProduct("manager")
                .setDeviceVersion("1.0")
                .setEventClassId("100")
                .setName("event name")
                .setSeverity((short) 11)
                .setExtension("src", "10.129.4.37")
                .setExtension("shost", "test-shost-12932js")
                .setExtension("dhost", "test-dhost-d88594")
                .setExtension("suser", "test.SE@testmail.ru");

        assert (Objects.equals(event.toString(), CEF1));


    }
}
