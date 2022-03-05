package de.hskl.itanalyst.addressauthorcartpublisherservice.repository;

import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.model.CartItemEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends CrudRepository<CartItemEntity, Long> {
}
