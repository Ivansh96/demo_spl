package ru.test.demo.spl.demo_spl.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class SchemaService {
    private final StorageService storageService;
    private final DeviceVendorService deviceVendorDirectoryService;

    public void sync() {
        deviceVendorDirectoryService.createMissingEntries(new HashSet<>(storageService.getExtensionFieldNames()));
    }
}
