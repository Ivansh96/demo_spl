package ru.test.demo.spl.demo_spl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.test.demo.spl.demo_spl.entity.ExtensionField;


import java.util.Set;
import java.util.UUID;

@Repository
public interface ExtensionFieldRepository extends JpaRepository<ExtensionField, UUID> {
    Set<ExtensionField> findAllByNameIn(Set<String> names);
    Set<ExtensionField> findAllByNameNotIn(Set<String> names);
    Set<ExtensionField> findAllByNameLikeIgnoreCase(String names);
}
