package de.hskl.itanalyst.BuchlagerBackendMonolith.PublisherTests;

import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.AddressEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.AuthorEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.BookEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.PublisherEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class PublisherPersistenceTests {
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
    }

    @Test
    public void whenDeletePublisher_thenDeleteBooksOfPublisher() {
        publisherRepository.delete(publisherEntity);
        Optional<List<PublisherEntity>> foundRepo = publisherRepository.findByNameContainingIgnoreCase(publisherEntity.getName());

        // Make sure publisher has been deleted
        assertThat(foundRepo).isEmpty();

        Iterable<BookEntity> allBooks = bookRepository.findAll();
        // Book has to be gone too
        assertThat(allBooks.iterator().hasNext()).isFalse();

        Iterable<AuthorEntity> allAuthors = authorRepository.findAll();
        // Author should still exist
        assertThat(allAuthors.iterator().hasNext()).isTrue();
    }

    @Test
    public void whenFindByName_thenReturnPublisher() {
        // given

        // when
        Optional<List<PublisherEntity>> foundRepo = publisherRepository.findByNameContainingIgnoreCase(publisherEntity.getName());

        // then
        assertThat(foundRepo).isPresent();
        foundRepo.ifPresent(publisherEntities -> {
            assertThat(publisherEntities.size()).isEqualTo(1);
            PublisherEntity entity = publisherEntities.iterator().next();
            assertThat(entity.getName()).isEqualTo(publisherEntity.getName());
            assertThat(entity.getAddress().getCity()).isEqualTo(addressEntity.getCity());
            assertThat(entity.getBooks().size()).isEqualTo(1);
            assertThat(entity.getBooks().iterator().next().getTitle()).isEqualTo("Mustertitel");
        });
    }

    @Test
    public void whenFindByNameContainingIgnoreCase_thenReturnPublisher() {
        // given

        // when
        Optional<List<PublisherEntity>> foundRepo = publisherRepository.findByNameContainingIgnoreCase("musterPUBlisher");

        // then
        assertThat(foundRepo).isPresent();
        foundRepo.ifPresent(publisherEntities -> {
            assertThat(publisherEntities.size()).isEqualTo(1);
            PublisherEntity entity = publisherEntities.iterator().next();
            assertThat(entity.getName()).isEqualTo(publisherEntity.getName());
            assertThat(entity.getAddress().getCity()).isEqualTo(addressEntity.getCity());
            assertThat(entity.getBooks().size()).isEqualTo(1);
            assertThat(entity.getBooks().iterator().next().getTitle()).isEqualTo("Mustertitel");
        });
    }

    @Test
    public void whenFindTwoByNameContainingIgnoreCase_thenReturnTwoPublisher() {
        // given
        PublisherEntity publisherEntity = new PublisherEntity(addressEntity, "musterpublisherNeu");
        publisherRepository.save(publisherEntity);

        // when
        Optional<List<PublisherEntity>> foundRepo = publisherRepository.findByNameContainingIgnoreCase("musterPUBlisher");

        // then
        assertThat(foundRepo).isPresent();
        foundRepo.ifPresent(publisherEntities -> {
            assertThat(publisherEntities.size()).isEqualTo(2);
            Iterator<PublisherEntity> it = publisherEntities.iterator();
            PublisherEntity entityOne = it.next();
            PublisherEntity entityTwo = it.next();
            assertThat(entityOne.getName()).isEqualTo("musterpublisher");
            assertThat(entityOne.getAddress().getCity()).isEqualTo(addressEntity.getCity());
            assertThat(entityOne.getBooks().size()).isEqualTo(1);
            assertThat(entityOne.getBooks().iterator().next().getTitle()).isEqualTo("Mustertitel");
            assertThat(entityTwo.getName()).isEqualTo("musterpublisherNeu");
            assertThat(entityTwo.getAddress().getCity()).isEqualTo(addressEntity.getCity());
            assertThat(entityTwo.getBooks().size()).isEqualTo(0);
        });
    }
}
