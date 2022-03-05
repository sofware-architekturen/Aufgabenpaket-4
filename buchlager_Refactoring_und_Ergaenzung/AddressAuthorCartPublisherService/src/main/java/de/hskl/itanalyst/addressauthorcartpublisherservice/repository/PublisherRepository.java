package de.hskl.itanalyst.addressauthorcartpublisherservice.repository;

import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.model.PublisherEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PublisherRepository extends CrudRepository<PublisherEntity, Long> {
    Optional<List<PublisherEntity>> findByNameContainingIgnoreCase(String name);

    Optional<PublisherEntity> findByNameIgnoreCase(String name);
}
