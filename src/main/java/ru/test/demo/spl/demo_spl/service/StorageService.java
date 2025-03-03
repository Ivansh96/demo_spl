package ru.test.demo.spl.demo_spl.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import org.jooq.*;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.gpn.platform.silverplate.event_collector.cef.lib.model.Event;
import ru.test.demo.spl.demo_spl.validation.EventDoesNotExistException;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.jooq.impl.DSL.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {

    private final DSLContext dslContext;
    @Qualifier("eventTableName")
    private final Name eventTableName;
    @Qualifier("columnsTableName")
    private final Name columnsTableName;

    private void insertEntry(Event event) throws TableDoesNotExistException, ColumnDoesNotExistException {
        InsertSetStep<Record> base = dslContext.insertInto(DSL.table(eventTableName));

        // Add basic fields
        Field<UUID> idField = field(name("b_id"), SQLDataType.UUID);
        Field<Long> recievedAtField = field(name("b_receivedAt"), SQLDataType.BIGINT);
        Field<Long> processedAtField = field(name("b_processedAt"), SQLDataType.BIGINT);

        // Add fields
        Field<Integer> versionField = field(name("c_version"), SQLDataType.INTEGER);
        Field<String> deviceVendorField = field(name("c_deviceVendor"), SQLDataType.VARCHAR(255));
        Field<String> deviceProductField = field(name("c_deviceProduct"), SQLDataType.VARCHAR(255));
        Field<String> deviceVersionField = field(name("c_deviceVersion"), SQLDataType.VARCHAR(255));
        Field<String> eventClassIdField = field(name("c_eventClassId"), SQLDataType.VARCHAR(255));
        Field<String> nameField = field(name("c_name"), SQLDataType.VARCHAR(255));
        Field<Integer> severityField = field(name("c_severity"), SQLDataType.INTEGER);

        InsertSetMoreStep<Record> next = base
            .set(idField, event.getId())
            .set(recievedAtField, event.getReceivedAt())
            .set(processedAtField, event.getProcessedAt())
            .set(versionField, event.getVersion())
            .set(deviceVendorField, event.getDeviceVendor())
            .set(deviceProductField, event.getDeviceProduct())
            .set(deviceVersionField, event.getDeviceVersion())
            .set(eventClassIdField, event.getEventClassId())
            .set(nameField, event.getName())
            .set(severityField, (int) event.getSeverity());

        // Add extensions
        for (Map.Entry<String, String> entry : event.getExtensions().entrySet()) {
            Field<String> extensionField = field(name(String.format("e_%s", entry.getKey())), SQLDataType.CLOB);
            next = next.set(extensionField, entry.getValue());
        }

        // Add metadata
        for (Map.Entry<String, Object> entry : event.getMetadata().entrySet()) {
            Field<String> metadataField = field(name(String.format("m_%s", entry.getKey())), SQLDataType.CLOB);
            next = next.set(metadataField, entry.getValue().toString());
        }

        try {
            next.execute();
        } catch (DataAccessException e) {
            createColumn(event);
        }
    }

    public List<String> getFieldNamesLike(String schemaName, String tableName, String pattern) {
        Table<Record> table = DSL.table(columnsTableName);
        Field<String> tableSchemaField = field(name("table_schema"), SQLDataType.CHAR(64));
        Field<String> tableNameField = field(name("table_name"), SQLDataType.CHAR(64));
        Field<String> columnNameField = field(name("column_name"), SQLDataType.CHAR(64));
        return dslContext
            .select(columnNameField)
            .from(table)
            .where(tableSchemaField.eq(schemaName))
            .and(tableNameField.eq(tableName))
            .and(columnNameField.like(pattern))
            .fetch(columnNameField);
    }

    public List<String> getFieldNamesWithPrefix(String schemaName, String tableName, String prefix) {
        return getFieldNamesLike(eventTableName.first(), eventTableName.last(), String.format("%s%%", prefix)).stream().map(s -> s.replaceAll(String.format("^%s", prefix), "")).toList();
    }

    public List<String> getExtensionFieldNames() {
        return getFieldNamesWithPrefix(eventTableName.first(), eventTableName.last(), "e_");
    }

    public List<String> getMetadataFieldNames() {
        return getFieldNamesWithPrefix(eventTableName.first(), eventTableName.last(), "m_");
    }

    public List<Integer> getVersions() {
        List<Integer> result = new ArrayList<>();
        try {
            Table<Record> table = DSL.table(eventTableName);
            Field<Integer> versionField = field(name("c_version"), SQLDataType.INTEGER);
            result = dslContext.select(versionField)
                .from(table)
                .groupBy(versionField)
                .fetch(versionField)
            ;
        } catch (DataAccessException e) {
            createTable();
        }
        return result;
    }

    public List<String> getDeviceVendorNames() {
        List<String> result = new ArrayList<>();
        try {
            Table<Record> table = DSL.table(eventTableName);
            Field<String> deviceVendorField = field(name("c_deviceVendor"), SQLDataType.VARCHAR(255));
            result = dslContext.select(deviceVendorField)
                .from(table)
                .groupBy(deviceVendorField)
                .fetch(deviceVendorField)
            ;
        } catch (DataAccessException e) {
            createTable();
        }
        return result;
    }

    public List<String> getDeviceProductNames() {
        List<String> result = new ArrayList<>();
        try {
            Table<Record> table = DSL.table(eventTableName);
            Field<String> deviceProductField = field(name("c_deviceProduct"), SQLDataType.VARCHAR(255));
            result = dslContext.select(deviceProductField)
                .from(table)
                .groupBy(deviceProductField)
                .fetch(deviceProductField)
            ;
        } catch (DataAccessException e) {
            createTable();
        }
        return result;
    }

    public List<String> getDeviceVersions() {
        List<String> result = new ArrayList<>();
        try {
            Table<Record> table = DSL.table(eventTableName);
            Field<String> deviceVersionField = field(name("c_deviceVersion"), SQLDataType.VARCHAR(255));
            result = dslContext.select(deviceVersionField)
                .from(table)
                .groupBy(deviceVersionField)
                .fetch(deviceVersionField)
            ;
        } catch (DataAccessException e) {
            createTable();
        }
        return result;
    }

    public List<String> getEventClassIds() {
        List<String> result = new ArrayList<>();
        try {
            Table<Record> table = DSL.table(eventTableName);
            Field<String> eventClassIdField = field(name("c_eventClassId"), SQLDataType.VARCHAR(255));
            result = dslContext.select(eventClassIdField)
                .from(table)
                .groupBy(eventClassIdField)
                .fetch(eventClassIdField)
            ;
        } catch (DataAccessException e) {
            createTable();
        }
        return result;
    }

    @AllArgsConstructor
    @Getter
    private static class ColumnDoesNotExistException extends Exception {
        private String columnName;
    }

    @AllArgsConstructor
    @Getter
    private static class TableDoesNotExistException extends Exception {
        private String tableName;
    }

    private void throwColumnDoesNotExistException(String message) throws ColumnDoesNotExistException {
        Pattern pattern = Pattern.compile("ERROR: column \"(?<name>.*)\" of relation \"(?<tableName>.*)\" does not exist");
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            throw new ColumnDoesNotExistException(matcher.group("name"));
        }
    }

    private void throwTableDoesNotExistException(String message) throws TableDoesNotExistException {
        Pattern pattern = Pattern.compile("ERROR: relation \"(?<name>.*)\" does not exist");
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            throw new TableDoesNotExistException(matcher.group("name"));
        }
    }

    private void createColumn(Event event, DataType<?> dataType) {
        for (Map.Entry<String, String> entry : event.getExtensions().entrySet()) {
            Field<String> extensionFields = field(name(String.format("e_%s", entry.getKey())), SQLDataType.CLOB);
            dslContext.alterTable(eventTableName).addColumnIfNotExists(field(name(extensionFields.getName()), dataType)).execute();
        }
        for (Map.Entry<String, Object> entry : event.getMetadata().entrySet()) {
            Field<String> metadataFields = field(name(String.format("m_%s", entry.getKey())), SQLDataType.CLOB);
            dslContext.alterTable(eventTableName).addColumnIfNotExists(field(name(metadataFields.getName()), dataType)).execute();
        }
    }

    private void createColumn(Event event) {
        createColumn(event, SQLDataType.CLOB);
    }

    private void createTable() {
        Table<Record> table = DSL.table(eventTableName);

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
    }

    public void storeEvent(Event event) throws EventDoesNotExistException {
        try {
            insertEntry(event);
        } catch (ColumnDoesNotExistException e) {
            createColumn(event);
            throw new EventDoesNotExistException("Column does not exist");
        } catch (TableDoesNotExistException e) {
            createTable();
            throw new EventDoesNotExistException("Table does not exist");
        }
    }

    public Result<Record> getEventResult(List<Integer> versions, List<String> deviceVendors, List<String> deviceProducts, List<String> deviceVersions, List<String> eventClassIds, Integer offset, Integer limit) {
        Condition condition = noCondition();

        if (!versions.isEmpty()) {
            condition = condition.and(field(name("c_version")).in(versions));
        }

        if (!deviceVendors.isEmpty()) {
            condition = condition.and(field(name("c_deviceVendor")).in(deviceVendors));
        }

        if (!deviceProducts.isEmpty()) {
            condition = condition.and(field(name("c_deviceProduct")).in(deviceProducts));
        }

        if (!deviceVersions.isEmpty()) {
            condition = condition.and(field(name("c_deviceVersion")).in(deviceVersions));
        }

        if (!eventClassIds.isEmpty()) {
            condition = condition.and(field(name("c_eventClassId")).in(eventClassIds));
        }

        return dslContext
            .select()
            .from(eventTableName)
            .where(condition)
            .orderBy(field(name("b_receivedAt")))
            .limit(limit)
            .offset(offset)
            .fetch();
    }
}
