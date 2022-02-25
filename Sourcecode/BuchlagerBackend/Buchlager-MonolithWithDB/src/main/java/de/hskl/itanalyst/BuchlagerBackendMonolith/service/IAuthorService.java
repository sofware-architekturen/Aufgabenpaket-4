package de.hskl.itanalyst.BuchlagerBackendMonolith.service;

import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.AuthorEntity;

import java.util.Optional;
import java.util.stream.Stream;

public interface IAuthorService {
    Optional<AuthorEntity> getAuthorById(final long authorId);

    AuthorEntity addOrUpdateAuthor(final AuthorEntity updatedAuthorEntity);

    boolean deleteAuthor(long authorId);

    Stream<AuthorEntity> getAllAuthorsAsStream();
}
