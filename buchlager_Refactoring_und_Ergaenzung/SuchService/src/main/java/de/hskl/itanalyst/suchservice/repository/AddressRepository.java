package de.hskl.itanalyst.suchservice.repository;

import de.hskl.itanalyst.suchservice.domain.model.AddressEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends CrudRepository<AddressEntity, Long> {
    Optional<List<AddressEntity>> findByCityContainingIgnoreCase(final String searchParameter);
}
