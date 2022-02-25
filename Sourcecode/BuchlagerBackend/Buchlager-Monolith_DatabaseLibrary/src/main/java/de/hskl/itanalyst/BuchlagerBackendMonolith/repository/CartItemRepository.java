package de.hskl.itanalyst.BuchlagerBackendMonolith.repository;

import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.CartItemEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends CrudRepository<CartItemEntity, Long> {
}
