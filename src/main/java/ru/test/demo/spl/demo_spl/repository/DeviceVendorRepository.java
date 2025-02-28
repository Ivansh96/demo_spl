package ru.test.demo.spl.demo_spl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.test.demo.spl.demo_spl.entity.DeviceVendor;


import java.util.Set;
import java.util.UUID;

@Repository
public interface DeviceVendorRepository extends JpaRepository<DeviceVendor, UUID> {
    Set<DeviceVendor> findAllByNameIn(Set<String> names);
    Set<DeviceVendor> findAllByNameNotIn(Set<String> names);
    Set<DeviceVendor> findAllByNameLikeIgnoreCase(String names);
}
