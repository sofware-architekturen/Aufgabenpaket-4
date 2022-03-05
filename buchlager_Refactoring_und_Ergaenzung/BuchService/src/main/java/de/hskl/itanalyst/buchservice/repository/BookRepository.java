package de.hskl.itanalyst.buchservice.repository;

import de.hskl.itanalyst.buchservice.domain.model.BookEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends CrudRepository<BookEntity, Long> {
    Optional<List<BookEntity>> findByTitleContainingIgnoreCase(final String searchParameter);
}
