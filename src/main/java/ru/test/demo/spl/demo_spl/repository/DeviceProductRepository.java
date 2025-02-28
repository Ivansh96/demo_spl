package ru.test.demo.spl.demo_spl.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.test.demo.spl.demo_spl.entity.DeviceProduct;


import java.util.Set;
import java.util.UUID;

@Repository
public interface DeviceProductRepository extends JpaRepository<DeviceProduct, UUID> {
    Set<DeviceProduct> findAllByNameIn(Set<String> names);
    Set<DeviceProduct> findAllByNameNotIn(Set<String> names);
    Set<DeviceProduct> findAllByNameLikeIgnoreCase(String names);
}
