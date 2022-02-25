package de.hskl.itanalyst.BuchlagerBackendMonolith.service.impl;

import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.AuthorEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.repository.AuthorRepository;
import de.hskl.itanalyst.BuchlagerBackendMonolith.service.IAuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class AuthorService implements IAuthorService {
    @Autowired
    private AuthorRepository authorRepository;

    public Optional<AuthorEntity> getAuthorById(final long authorId) {
        final Optional<AuthorEntity> authorEntity = authorRepository.findById(authorId);
        return authorEntity;
    }

    public AuthorEntity addOrUpdateAuthor(final AuthorEntity updatedAuthorEntity) {
        return authorRepository.save(updatedAuthorEntity);
    }

    public boolean deleteAuthor(long authorId) {
        final Optional<AuthorEntity> authorEntity = authorRepository.findById(authorId);
        try {
            if (authorEntity.isPresent()) {
                authorRepository.delete(authorEntity.get());
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public Stream<AuthorEntity> getAllAuthorsAsStream() {
        final Iterable<AuthorEntity> allAuthors = authorRepository.findAll();
        final Stream<AuthorEntity> authorEntityStreamHelper = StreamSupport.stream(allAuthors.spliterator(), false);

        return authorEntityStreamHelper;
    }
}
