package ru.test.demo.spl.demo_spl.entity;

public interface NameAwareEntity<SELF extends NameAwareEntity<SELF>> {
    String getName();
    SELF setName(String name);
}
