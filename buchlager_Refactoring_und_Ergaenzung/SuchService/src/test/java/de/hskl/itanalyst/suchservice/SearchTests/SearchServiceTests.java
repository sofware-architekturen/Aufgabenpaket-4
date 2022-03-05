package de.hskl.itanalyst.suchservice.SearchTests;

import de.hskl.itanalyst.suchservice.domain.model.AddressEntity;
import de.hskl.itanalyst.suchservice.domain.model.AuthorEntity;
import de.hskl.itanalyst.suchservice.domain.model.BookEntity;
import de.hskl.itanalyst.suchservice.domain.model.PublisherEntity;
import de.hskl.itanalyst.suchservice.repository.AddressRepository;
import de.hskl.itanalyst.suchservice.repository.AuthorRepository;
import de.hskl.itanalyst.suchservice.repository.BookRepository;
import de.hskl.itanalyst.suchservice.repository.PublisherRepository;
import de.hskl.itanalyst.suchservice.service.ISearchService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SearchServiceTests {
    @Autowired
    private ISearchService searchService;

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private AddressRepository addressRepository;

    @MockBean
    private PublisherRepository publisherRepository;

    private final AuthorEntity authorEntity = new AuthorEntity("Max", "Mustermann", new HashSet<>(1));
    private final AddressEntity addressEntity = new AddressEntity("musterhausen");
    private final PublisherEntity publisherEntity = new PublisherEntity(addressEntity, new HashSet<>(1), "Musterverlag");
    private final BookEntity bookEntity = new BookEntity(publisherEntity, Set.of(authorEntity), "Mustertitel", 10);
    private final BookEntity bookEntity2 = new BookEntity(publisherEntity, Set.of(authorEntity), "Mustertitel2", 10);

    @BeforeEach
    public void setUp() {
        Mockito.when(authorRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("test", "test")).thenReturn(Optional.of(List.of(authorEntity)));
        Mockito.when(authorRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase("test1", "test1")).thenReturn(Optional.empty());

        Mockito.when(publisherRepository.findByNameContainingIgnoreCase("test")).thenReturn(Optional.of(List.of(publisherEntity)));
        Mockito.when(publisherRepository.findByNameContainingIgnoreCase("test1")).thenReturn(Optional.empty());

        Mockito.when(bookRepository.findByTitleContainingIgnoreCase("test")).thenReturn(Optional.of(List.of(bookEntity)));
        Mockito.when(bookRepository.findByTitleContainingIgnoreCase("test1")).thenReturn(Optional.empty());

        Mockito.when(addressRepository.findByCityContainingIgnoreCase("test")).thenReturn(Optional.of(List.of(addressEntity)));
        Mockito.when(addressRepository.findByCityContainingIgnoreCase("test1")).thenReturn(Optional.empty());
    }

    @AfterEach
    public void afterEach() {
        // reset mock
        Mockito.reset(authorRepository);
        Mockito.reset(bookRepository);
        Mockito.reset(addressRepository);
        Mockito.reset(publisherRepository);
    }

    @Test
    public void whenSearchForAuthors_thenReturnFoundAuthor() {
        List<AuthorEntity> findings = searchService.searchAuthorsForString("test");

        assertThat(findings.size()).isEqualTo(1);
    }

    @Test
    public void whenSearchForNonExistingAuthors_thenReturnEmptyList() {
        List<AuthorEntity> findings = searchService.searchAuthorsForString("test1");

        assertThat(findings.size()).isEqualTo(0);
    }

    @Test
    public void whenSearchForPublishers_thenReturnFoundPublisher() {
        List<PublisherEntity> findings = searchService.searchPublishersForString("test");

        assertThat(findings.size()).isEqualTo(1);
    }

    @Test
    public void whenSearchForNonExistingPublisher_thenReturnEmptyList() {
        List<PublisherEntity> findings = searchService.searchPublishersForString("test1");

        assertThat(findings.size()).isEqualTo(0);
    }

    @Test
    public void whenSearchForBooks_thenReturnFoundBook() {
        List<BookEntity> findings = searchService.searchBooksForString("test");

        assertThat(findings.size()).isEqualTo(1);
    }

    @Test
    public void whenSearchForNonExistingBook_thenReturnEmptyList() {
        List<BookEntity> findings = searchService.searchBooksForString("test1");

        assertThat(findings.size()).isEqualTo(0);
    }

    @Test
    public void whenSearchForAddress_thenReturnFoundAddress() {
        List<AddressEntity> findings = searchService.searchAddressesForString("test");

        assertThat(findings.size()).isEqualTo(1);
    }

    @Test
    public void whenSearchForNonExistingAddress_thenReturnEmptyList() {
        List<AddressEntity> findings = searchService.searchAddressesForString("test1");

        assertThat(findings.size()).isEqualTo(0);
    }
}
