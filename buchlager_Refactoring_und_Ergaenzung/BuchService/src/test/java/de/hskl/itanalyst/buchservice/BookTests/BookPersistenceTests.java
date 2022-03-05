package de.hskl.itanalyst.buchservice.BookTests;

import de.hskl.itanalyst.buchservice.domain.model.AddressEntity;
import de.hskl.itanalyst.buchservice.domain.model.AuthorEntity;
import de.hskl.itanalyst.buchservice.domain.model.BookEntity;
import de.hskl.itanalyst.buchservice.domain.model.PublisherEntity;
import de.hskl.itanalyst.buchservice.repository.AddressRepository;
import de.hskl.itanalyst.buchservice.repository.AuthorRepository;
import de.hskl.itanalyst.buchservice.repository.BookRepository;
import de.hskl.itanalyst.buchservice.repository.PublisherRepository;
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
public class BookPersistenceTests {
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
    public void whenDeleteBook_thenJustDeleteTheBook() {
        bookRepository.delete(bookEntity);

        Optional<List<BookEntity>> foundRepo = bookRepository.findByTitleContainingIgnoreCase(bookEntity.getTitle());

        // Make sure the book has been deleted
        assertThat(foundRepo).isEmpty();

        Iterable<PublisherEntity> allPublishers = publisherRepository.findAll();
        // Publisher should still exist
        assertThat(allPublishers.iterator().hasNext()).isTrue();

        Iterable<AuthorEntity> allAuthors = authorRepository.findAll();
        // Author should still exist
        assertThat(allAuthors.iterator().hasNext()).isTrue();
    }

    @Test
    public void whenFindByTitle_thenReturnBook() {
        // given

        // when
        Optional<List<BookEntity>> foundRepo = bookRepository.findByTitleContainingIgnoreCase(bookEntity.getTitle());

        // then
        assertThat(foundRepo).isPresent();
        foundRepo.ifPresent(bookEntities -> {
            assertThat(bookEntities.size()).isEqualTo(1);
            BookEntity entity = bookEntities.iterator().next();
            assertThat(entity.getTitle()).isEqualTo(bookEntity.getTitle());
            assertThat(entity.getPublisher().getName()).isEqualTo(publisherEntity.getName());
            assertThat(entity.getPublisher().getAddress().getCity()).isEqualTo(publisherEntity.getAddress().getCity());
            assertThat(entity.getAuthors().size()).isEqualTo(1);
            assertThat(entity.getAuthors().iterator().next().getFirstName()).isEqualTo(authorEntity.getFirstName());
        });
    }

    @Test
    public void whenFindByTitleContainingIgnoreCase_thenReturnBook() {
        // given

        // when
        Optional<List<BookEntity>> foundRepo = bookRepository.findByTitleContainingIgnoreCase("MuStERTitel");

        // then
        assertThat(foundRepo).isPresent();
        foundRepo.ifPresent(bookEntities -> {
            assertThat(bookEntities.size()).isEqualTo(1);
            BookEntity entity = bookEntities.iterator().next();
            assertThat(entity.getTitle()).isEqualTo(bookEntity.getTitle());
            assertThat(entity.getPublisher().getName()).isEqualTo(publisherEntity.getName());
            assertThat(entity.getPublisher().getAddress().getCity()).isEqualTo(publisherEntity.getAddress().getCity());
            assertThat(entity.getAuthors().size()).isEqualTo(1);
            assertThat(entity.getAuthors().iterator().next().getFirstName()).isEqualTo(authorEntity.getFirstName());
        });
    }

    @Test
    public void whenFindTwoByTitleContainingIgnoreCase_thenReturnTwoBooks() {
        // given
        BookEntity bookEntityNew = new BookEntity(publisherEntity, new HashSet<>(List.of(authorEntity)), "MustertitelTitel", 10);
        bookRepository.save(bookEntityNew);

        // when
        Optional<List<BookEntity>> foundRepo = bookRepository.findByTitleContainingIgnoreCase("MuStERTitel");

        // then
        assertThat(foundRepo).isPresent();
        foundRepo.ifPresent(bookEntities -> {
            assertThat(bookEntities.size()).isEqualTo(2);
            Iterator<BookEntity> it = bookEntities.iterator();
            BookEntity entityOne = it.next();
            BookEntity entityTwo = it.next();
            assertThat(entityOne.getTitle()).isEqualTo(bookEntity.getTitle());
            assertThat(entityOne.getPublisher().getName()).isEqualTo(publisherEntity.getName());
            assertThat(entityOne.getPublisher().getAddress().getCity()).isEqualTo(publisherEntity.getAddress().getCity());
            assertThat(entityOne.getAuthors().size()).isEqualTo(1);
            assertThat(entityOne.getAuthors().iterator().next().getFirstName()).isEqualTo(authorEntity.getFirstName());
            assertThat(entityTwo.getTitle()).isEqualTo(bookEntityNew.getTitle());
            assertThat(entityTwo.getPublisher().getName()).isEqualTo(publisherEntity.getName());
            assertThat(entityTwo.getPublisher().getAddress().getCity()).isEqualTo(publisherEntity.getAddress().getCity());
            assertThat(entityTwo.getAuthors().size()).isEqualTo(1);
            assertThat(entityTwo.getAuthors().iterator().next().getFirstName()).isEqualTo(authorEntity.getFirstName());
        });
    }
}
