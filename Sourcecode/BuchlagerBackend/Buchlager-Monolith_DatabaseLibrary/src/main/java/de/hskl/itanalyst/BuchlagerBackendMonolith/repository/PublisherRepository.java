package de.hskl.itanalyst.BuchlagerBackendMonolith.repository;

import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.PublisherEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PublisherRepository extends CrudRepository<PublisherEntity, Long> {
    Optional<List<PublisherEntity>> findByNameContainingIgnoreCase(String name);

    Optional<PublisherEntity> findByNameIgnoreCase(String name);
}
