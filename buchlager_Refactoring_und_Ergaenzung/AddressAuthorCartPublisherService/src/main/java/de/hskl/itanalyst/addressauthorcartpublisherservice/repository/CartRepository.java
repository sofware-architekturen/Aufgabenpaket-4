package de.hskl.itanalyst.addressauthorcartpublisherservice.repository;

import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.model.CartEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CartRepository extends CrudRepository<CartEntity, Long> {
    Optional<CartEntity> findBySessionId(final String sessionId);

    void deleteByValidUntilBefore(final LocalDateTime timestamp);
}
