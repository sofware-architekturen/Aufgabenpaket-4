package de.hskl.itanalyst.BuchlagerBackendMonolith.AuthorTests;

import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.AddressEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.AuthorEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.BookEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.PublisherEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.repository.AddressRepository;
import de.hskl.itanalyst.BuchlagerBackendMonolith.repository.AuthorRepository;
import de.hskl.itanalyst.BuchlagerBackendMonolith.repository.BookRepository;
import de.hskl.itanalyst.BuchlagerBackendMonolith.repository.PublisherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class AuthorPersistenceTests {
    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    private AddressEntity addressEntity;
    private BookEntity bookEntity;
    private PublisherEntity publisherEntity;
    private AuthorEntity authorEntity;

    @Test
    public void contextLoads() throws Exception {
        assertThat(publisherRepository).isNotNull();
        assertThat(addressRepository).isNotNull();
        assertThat(bookRepository).isNotNull();
        assertThat(authorRepository).isNotNull();
    }

    @BeforeEach
    public void beforeEach() {
        addressEntity = new AddressEntity("mustercity");
        addressRepository.save(addressEntity);
        publisherEntity = new PublisherEntity(addressEntity, "musterpublisher");
        publisherEntity = publisherRepository.save(publisherEntity);
        authorEntity = new AuthorEntity("Max", "Mustermann");
        authorEntity = authorRepository.save(authorEntity);
        bookEntity = new BookEntity(publisherEntity, new HashSet<>(List.of(authorEntity)), "Mustertitel", 10);
        bookEntity = bookRepository.save(bookEntity);

        authorEntity = authorRepository.save(authorEntity);
        publisherEntity = publisherRepository.findById(publisherEntity.getId()).get();
        publisherEntity.addBook(bookEntity);
        publisherEntity = publisherRepository.save(publisherEntity);
        publisherEntity = publisherRepository.findById(publisherEntity.getId()).get();
        bookEntity = bookRepository.findById(bookEntity.getId()).get();
    }

    @Test
    public void whenDeleteAuthor_thenDeleteTheAuthorAndTheBook() {
        authorRepository.delete(authorEntity);

        Iterable<AuthorEntity> allAuthors = authorRepository.findAll();
        // Author should not exist anymore
        assertThat(allAuthors.iterator().hasNext()).isFalse();

        Iterable<BookEntity> allBooks = bookRepository.findAll();
        // Make sure the book has been deleted
        assertThat(allBooks.iterator().hasNext()).isFalse();

        Iterable<PublisherEntity> allPublishers = publisherRepository.findAll();
        // Publisher should still exist
        assertThat(allPublishers.iterator().hasNext()).isTrue();
    }

    @Test
    public void whenFindByFirstNameContainingOrLastNameContaining_thenReturnAuthor() {
        // given

        // when
        Optional<List<AuthorEntity>> foundRepo = authorRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("m", "m");

        // then
        assertThat(foundRepo).isPresent();
        foundRepo.ifPresent(authorEntityList -> {
            assertThat(authorEntityList.size()).isEqualTo(1);
            AuthorEntity entity = authorEntityList.get(0);
            assertThat(entity.getFirstName()).isEqualTo(authorEntity.getFirstName());
            assertThat(entity.getLastName()).isEqualTo(authorEntity.getLastName());
            assertThat(entity.getId()).isEqualTo(authorEntity.getId());
            assertThat(entity.getBooks().size()).isEqualTo(authorEntity.getBooks().size());
        });

        // when
        foundRepo = authorRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("Max", "m");

        // then
        assertThat(foundRepo).isPresent();
        foundRepo.ifPresent(authorEntityList -> {
            assertThat(authorEntityList.size()).isEqualTo(1);
            AuthorEntity entity = authorEntityList.get(0);
            assertThat(entity.getFirstName()).isEqualTo(authorEntity.getFirstName());
            assertThat(entity.getLastName()).isEqualTo(authorEntity.getLastName());
            assertThat(entity.getId()).isEqualTo(authorEntity.getId());
            assertThat(entity.getBooks().size()).isEqualTo(authorEntity.getBooks().size());
        });

        // when
        foundRepo = authorRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("Max", "Mustermann");

        // then
        assertThat(foundRepo).isPresent();
        foundRepo.ifPresent(authorEntityList -> {
            assertThat(authorEntityList.size()).isEqualTo(1);
            AuthorEntity entity = authorEntityList.get(0);
            assertThat(entity.getFirstName()).isEqualTo(authorEntity.getFirstName());
            assertThat(entity.getLastName()).isEqualTo(authorEntity.getLastName());
            assertThat(entity.getId()).isEqualTo(authorEntity.getId());
            assertThat(entity.getBooks().size()).isEqualTo(authorEntity.getBooks().size());
        });
    }

    @Test
    public void whenFindByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase_thenReturnAuthor() {
        // given

        // when
        Optional<List<AuthorEntity>> foundRepo = authorRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("M", "M");

        // then
        assertThat(foundRepo).isPresent();
        foundRepo.ifPresent(authorEntityList -> {
            assertThat(authorEntityList.size()).isEqualTo(1);
            AuthorEntity entity = authorEntityList.get(0);
            assertThat(entity.getFirstName()).isEqualTo(authorEntity.getFirstName());
            assertThat(entity.getLastName()).isEqualTo(authorEntity.getLastName());
            assertThat(entity.getId()).isEqualTo(authorEntity.getId());
            assertThat(entity.getBooks().size()).isEqualTo(authorEntity.getBooks().size());
        });

        // when
        foundRepo = authorRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("MaX", "mU");

        // then
        assertThat(foundRepo).isPresent();
        foundRepo.ifPresent(authorEntityList -> {
            assertThat(authorEntityList.size()).isEqualTo(1);
            AuthorEntity entity = authorEntityList.get(0);
            assertThat(entity.getFirstName()).isEqualTo(authorEntity.getFirstName());
            assertThat(entity.getLastName()).isEqualTo(authorEntity.getLastName());
            assertThat(entity.getId()).isEqualTo(authorEntity.getId());
            assertThat(entity.getBooks().size()).isEqualTo(authorEntity.getBooks().size());
        });

        // when
        foundRepo = authorRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("Max", "MustERMann");

        // then
        assertThat(foundRepo).isPresent();
        foundRepo.ifPresent(authorEntityList -> {
            assertThat(authorEntityList.size()).isEqualTo(1);
            AuthorEntity entity = authorEntityList.get(0);
            assertThat(entity.getFirstName()).isEqualTo(authorEntity.getFirstName());
            assertThat(entity.getLastName()).isEqualTo(authorEntity.getLastName());
            assertThat(entity.getId()).isEqualTo(authorEntity.getId());
            assertThat(entity.getBooks().size()).isEqualTo(authorEntity.getBooks().size());
        });
    }

    @Test
    public void whenFindByWrongFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase_thenReturnNoAuthor() {
        // given

        // when
        Optional<List<AuthorEntity>> foundRepo = authorRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("U", "z");

        // then
        assertThat(foundRepo).isEmpty();
    }
}
