package ru.test.demo.spl.demo_spl.service;

import java.util.Set;

public interface DirectoryService<T extends Object> {
    void createMissingEntries(Set<String> currentNames);
}
