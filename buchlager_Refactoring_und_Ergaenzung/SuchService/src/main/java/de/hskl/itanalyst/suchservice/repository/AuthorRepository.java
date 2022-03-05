package de.hskl.itanalyst.suchservice.repository;

import de.hskl.itanalyst.suchservice.domain.model.AuthorEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends CrudRepository<AuthorEntity, Long> {
    Optional<List<AuthorEntity>> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(final String searchParameterFirstName, final String searchParameterLastName);
}
