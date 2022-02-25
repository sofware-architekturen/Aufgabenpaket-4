package de.hskl.itanalyst.BuchlagerBackendMonolith.repository;

import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.BuchlagerEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuchlagerRepository extends CrudRepository<BuchlagerEntity, Long> {
}
