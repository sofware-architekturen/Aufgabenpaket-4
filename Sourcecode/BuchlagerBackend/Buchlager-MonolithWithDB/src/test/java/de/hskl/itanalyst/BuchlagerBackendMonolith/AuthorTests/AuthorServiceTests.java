package de.hskl.itanalyst.BuchlagerBackendMonolith.AuthorTests;

import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.AuthorEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.repository.AuthorRepository;
import de.hskl.itanalyst.BuchlagerBackendMonolith.service.IAuthorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AuthorServiceTests {
    @Autowired
    private IAuthorService authorService;

    @MockBean
    private AuthorRepository authorRepository;

    private final AuthorEntity authorEntity = new AuthorEntity("Max", "Mustermann", Collections.EMPTY_SET);

    @BeforeEach
    public void setUp() {
        Mockito.when(authorRepository.findById(0L)).thenReturn(Optional.of(authorEntity));
        Spliterator<AuthorEntity> spliterator = Arrays.spliterator(new AuthorEntity[]{authorEntity});
        Iterable<AuthorEntity> authorEntityIterable = new Iterable<AuthorEntity>() {
            @Override
            public Spliterator<AuthorEntity> spliterator() {
                return spliterator;
            }

            @Override
            public Iterator<AuthorEntity> iterator() {
                return new Iterator<AuthorEntity>() {
                    private boolean hasNext = true;

                    @Override
                    public boolean hasNext() {
                        return hasNext;
                    }

                    @Override
                    public AuthorEntity next() {
                        try {
                            return authorEntity;
                        } finally {
                            hasNext = false;
                        }
                    }
                };
            }
        };

        Mockito.when(authorRepository.findAll()).thenReturn(authorEntityIterable);
    }

    @AfterEach
    public void afterEach() {
        // reset mock
        Mockito.reset(authorRepository);
    }

    @Test
    public void whenValidId_thenAuthorShouldBeFound() {
        Optional<AuthorEntity> foundService = authorService.getAuthorById(0L);

        assertThat(foundService).isPresent();
        foundService.ifPresent(entity -> assertThat(entity.getFirstName()).isEqualTo(authorEntity.getFirstName()));
        foundService.ifPresent(entity -> assertThat(entity.getLastName()).isEqualTo(authorEntity.getLastName()));
    }

    @Test
    public void whenGetAllAuthors_thenAuthorsCountShouldBeGreater0() {
        Stream<AuthorEntity> foundStream = authorService.getAllAuthorsAsStream();
        assertThat(foundStream).isNotNull();
    }

    @Test
    public void whenAddOrUpdateAuthorAddAuthor_thenShouldReturnAuthor() {
        Mockito.when(authorRepository.save(authorEntity)).thenReturn(authorEntity);
        assertThat(authorService.addOrUpdateAuthor(authorEntity)).isEqualTo(authorEntity);
    }

    @Test
    public void whenAddOrUpdateAuthorUpdateAuthor_thenShouldReturnUpdatedAuthor() {
        Mockito.when(authorRepository.save(authorEntity)).thenReturn(authorEntity);
        assertThat(authorService.addOrUpdateAuthor(authorEntity)).isEqualTo(authorEntity);
        Mockito.verify(authorRepository, VerificationModeFactory.atLeast(1)).save(authorEntity);
    }

    @Test
    public void whenDeleteAuthor_thenShouldReturnTrueIfSuccess() {
        Mockito.when(authorRepository.findById(0L)).thenReturn(Optional.of(authorEntity));
        assertThat(authorService.deleteAuthor(authorEntity.getId())).isTrue();
        Mockito.verify(authorRepository, VerificationModeFactory.atLeast(1)).delete(authorEntity);
    }

    @Test
    public void whenDeleteAuthor_thenShouldReturnFalseIfFailed() {
        Mockito.when(authorRepository.findById(0L)).thenReturn(Optional.empty());
        assertThat(authorService.deleteAuthor(authorEntity.getId())).isFalse();
    }

    @Test
    public void whenDeleteAuthor_thenShouldReturnExceptionIfDBFailed() {
        Mockito.doThrow(new IllegalArgumentException("whenDeleteAuthor_thenShouldReturnExceptionIfDBFailed")).when(authorRepository).delete(authorEntity);
        assertThat(authorService.deleteAuthor(authorEntity.getId())).isFalse();
    }
}
