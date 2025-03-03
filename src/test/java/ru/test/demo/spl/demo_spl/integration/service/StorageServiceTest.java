package ru.test.demo.spl.demo_spl.integration.service;

import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.test.context.ActiveProfiles;
import ru.test.demo.spl.demo_spl.configuration.Properties;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.name;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JooqTest
@ActiveProfiles("test")
public class StorageServiceTest {

    @Autowired
    private Properties properties;
    @Autowired
    private DSLContext dslContext;


    @Test
    void testDSLContext() {
        assertThat(this.dslContext.selectCount().from(properties.getIntegration().getInformationSchema()).fetchOne(0, Integer.class))
                .isGreaterThan(0);
    }

    @Test
    @DisplayName("Creating table")
    void getDefaultTableNameFromProperties() {

        Table<Record> table = DSL.table(properties.getIntegration().getEventTableName());
        assertEquals("main", table.getName());

    }

    @Test
    @DisplayName("Creating table")
    void getFieldsNameAndType() {

        Field<UUID> idField = field(name("b_id"), SQLDataType.UUID);
        Field<Long> recievedAtField = field(name("b_receivedAt"), SQLDataType.BIGINT);
        Field<Long> processedAtField = field(name("b_processedAt"), SQLDataType.BIGINT);

        Field<Integer> versionField = field(name("c_version"), SQLDataType.INTEGER);
        Field<String> deviceVendorField = field(name("c_deviceVendor"), SQLDataType.VARCHAR(255));
        Field<String> deviceProductField = field(name("c_deviceProduct"), SQLDataType.VARCHAR(255));
        Field<String> deviceVersionField = field(name("c_deviceVersion"), SQLDataType.VARCHAR(255));
        Field<String> eventClassIdField = field(name("c_eventClassId"), SQLDataType.VARCHAR(255));
        Field<String> nameField = field(name("c_name"), SQLDataType.VARCHAR(255));
        Field<Integer> severityField = field(name("c_severity"), SQLDataType.INTEGER);

        assertEquals("b_id", idField.getName());
        assertEquals(SQLDataType.UUID, idField.getDataType());

        assertEquals("b_receivedAt", recievedAtField.getName());
        assertEquals(SQLDataType.BIGINT, recievedAtField.getDataType());

        assertEquals("b_processedAt", processedAtField.getName());
        assertEquals(SQLDataType.BIGINT, processedAtField.getDataType());

        assertEquals("c_version", versionField.getName());
        assertEquals(SQLDataType.INTEGER, versionField.getDataType());

        assertEquals("c_deviceVendor", deviceVendorField.getName());
        assertEquals(SQLDataType.VARCHAR, deviceVendorField.getDataType());

        assertEquals("c_deviceProduct", deviceProductField.getName());
        assertEquals(SQLDataType.VARCHAR, deviceProductField.getDataType());

        assertEquals("c_deviceVersion", deviceVersionField.getName());
        assertEquals(SQLDataType.VARCHAR, deviceVersionField.getDataType());

        assertEquals("c_eventClassId", eventClassIdField.getName());
        assertEquals(SQLDataType.VARCHAR, eventClassIdField.getDataType());

        assertEquals("c_name", nameField.getName());
        assertEquals(SQLDataType.VARCHAR, nameField.getDataType());

        assertEquals("c_severity", severityField.getName());
        assertEquals(SQLDataType.INTEGER, severityField.getDataType());


    }
    @Test
    @DisplayName("Creating table")
    void getFullTable() {

        Table<Record> table = DSL.table(properties.getIntegration().getEventTableName());

        Field<UUID> idField = field(name("b_id"), SQLDataType.UUID);
        Field<Long> recievedAtField = field(name("b_receivedAt"), SQLDataType.BIGINT);
        Field<Long> processedAtField = field(name("b_processedAt"), SQLDataType.BIGINT);

        Field<Integer> versionField = field(name("c_version"), SQLDataType.INTEGER);
        Field<String> deviceVendorField = field(name("c_deviceVendor"), SQLDataType.VARCHAR(255));
        Field<String> deviceProductField = field(name("c_deviceProduct"), SQLDataType.VARCHAR(255));
        Field<String> deviceVersionField = field(name("c_deviceVersion"), SQLDataType.VARCHAR(255));
        Field<String> eventClassIdField = field(name("c_eventClassId"), SQLDataType.VARCHAR(255));
        Field<String> nameField = field(name("c_name"), SQLDataType.VARCHAR(255));
        Field<Integer> severityField = field(name("c_severity"), SQLDataType.INTEGER);

        dslContext.createTableIfNotExists(table)
                .column(idField)
                .column(recievedAtField)
                .column(processedAtField)
                .column(versionField)
                .column(deviceVendorField)
                .column(deviceProductField)
                .column(deviceVersionField)
                .column(eventClassIdField)
                .column(nameField)
                .column(severityField)
                .execute();

        final Result<Record10<UUID, Long, Long, Integer, String, String, String, String, String, Integer>> result =
                dslContext.select(idField, recievedAtField, processedAtField,
                                versionField, deviceVendorField, deviceProductField, deviceVersionField,
                                eventClassIdField, nameField, severityField)
                        .from(table)
                        .fetch();

        assertEquals(10, result.size());

        assertEquals("b_id", result.get(0).field1().getName());
        assertEquals("b_receivedAt", result.get(1).field2().getName());
        assertEquals("b_processedAt", result.get(2).field3().getName());
        assertEquals("c_version", result.get(3).field4().getName());
        assertEquals("c_deviceVendor", result.get(4).field5().getName());
        assertEquals("c_deviceProduct", result.get(5).field6().getName());
        assertEquals("c_deviceVersion", result.get(6).field7().getName());
        assertEquals("c_eventClassId", result.get(7).field8().getName());
        assertEquals("c_name", result.get(8).field9().getName());
        assertEquals("c_severity", result.get(9).field10().getName());


    }


}
