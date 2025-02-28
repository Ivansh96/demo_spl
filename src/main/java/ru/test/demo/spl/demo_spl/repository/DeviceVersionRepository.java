package ru.test.demo.spl.demo_spl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.test.demo.spl.demo_spl.entity.DeviceVersion;


import java.util.Set;
import java.util.UUID;

@Repository
public interface DeviceVersionRepository extends JpaRepository<DeviceVersion, UUID> {
    Set<DeviceVersion> findAllByNameIn(Set<String> names);
    Set<DeviceVersion> findAllByNameNotIn(Set<String> names);
    Set<DeviceVersion> findAllByNameLikeIgnoreCase(String names);
}
