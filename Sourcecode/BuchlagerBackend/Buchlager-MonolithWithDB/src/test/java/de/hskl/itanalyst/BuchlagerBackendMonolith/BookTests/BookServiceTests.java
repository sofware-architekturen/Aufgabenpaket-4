package de.hskl.itanalyst.BuchlagerBackendMonolith.BookTests;

import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.AddressEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.AuthorEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.BookEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.PublisherEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.repository.BookRepository;
import de.hskl.itanalyst.BuchlagerBackendMonolith.service.IBookService;
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
public class BookServiceTests {
    @Autowired
    private IBookService bookService;

    @MockBean
    private BookRepository bookRepository;

    private final AuthorEntity authorEntity = new AuthorEntity("Max", "Mustermann", new HashSet<>(1));
    private final AddressEntity addressEntity = new AddressEntity("musterhausen");
    private final PublisherEntity publisherEntity = new PublisherEntity(addressEntity, new HashSet<>(1), "Musterverlag");
    private final BookEntity bookEntity = new BookEntity(publisherEntity, Set.of(authorEntity), "Mustertitel", 10);

    @BeforeEach
    public void setUp() {
        Mockito.when(bookRepository.findById(0L)).thenReturn(Optional.of(bookEntity));
        Spliterator<BookEntity> spliterator = Arrays.spliterator(new BookEntity[]{bookEntity});
        Iterable<BookEntity> authorEntityIterable = new Iterable<BookEntity>() {
            @Override
            public Spliterator<BookEntity> spliterator() {
                return spliterator;
            }

            @Override
            public Iterator<BookEntity> iterator() {
                return new Iterator<BookEntity>() {
                    private boolean hasNext = true;

                    @Override
                    public boolean hasNext() {
                        return hasNext;
                    }

                    @Override
                    public BookEntity next() {
                        try {
                            return bookEntity;
                        } finally {
                            hasNext = false;
                        }
                    }
                };
            }
        };

        Mockito.when(bookRepository.findAll()).thenReturn(authorEntityIterable);
        // Mockito.when(publisherRepository.save(publisherEntityUpdatedName)).thenReturn(publisherEntityUpdatedName);
    }

    @AfterEach
    public void afterEach() {
        // reset mock
        Mockito.reset(bookRepository);
    }

    @Test
    public void whenValidId_thenBookShouldBeFound() {
        Optional<BookEntity> foundService = bookService.getBookById(0L);

        assertThat(foundService).isPresent();
        foundService.ifPresent(entity -> assertThat(entity.getTitle()).isEqualTo(bookEntity.getTitle()));
        foundService.ifPresent(entity -> assertThat(entity.getAuthors().size()).isEqualTo(bookEntity.getAuthors().size()));
        foundService.ifPresent(entity -> assertThat(entity.getPublisher().getName()).isEqualTo(bookEntity.getPublisher().getName()));
    }

    @Test
    public void whenGetAllBooks_thenBooksCountShouldBeGreater0() {
        Stream<BookEntity> foundStream = bookService.getAllBooksAsStream();
        assertThat(foundStream).isNotNull();
    }

    @Test
    public void whenAddOrUpdateBookAddBook_thenShouldReturnBook() {
        Mockito.when(bookRepository.save(bookEntity)).thenReturn(bookEntity);
        assertThat(bookService.addOrUpdateBook(bookEntity)).isEqualTo(bookEntity);
    }

    @Test
    public void whenAddOrUpdateBookUpdateBook_thenShouldReturnUpdatedBook() {
        Mockito.when(bookRepository.save(bookEntity)).thenReturn(bookEntity);
        assertThat(bookService.addOrUpdateBook(bookEntity)).isEqualTo(bookEntity);
        Mockito.verify(bookRepository, VerificationModeFactory.atLeast(1)).save(bookEntity);
    }

    @Test
    public void whenDeleteBook_thenShouldReturnTrueIfSuccess() {
        Mockito.when(bookRepository.findById(0L)).thenReturn(Optional.of(bookEntity));
        assertThat(bookService.deleteBook(bookEntity.getId())).isTrue();
        Mockito.verify(bookRepository, VerificationModeFactory.atLeast(1)).delete(bookEntity);
    }

    @Test
    public void whenDeleteBook_thenShouldReturnFalseIfFailed() {
        Mockito.when(bookRepository.findById(0L)).thenReturn(Optional.empty());
        assertThat(bookService.deleteBook(bookEntity.getId())).isFalse();
    }

    @Test
    public void whenDeleteBook_thenShouldReturnExceptionIfDBFailed() {
        Mockito.doThrow(new IllegalArgumentException("whenDeleteBook_thenShouldReturnExceptionIfDBFailed")).when(bookRepository).delete(bookEntity);
        assertThat(bookService.deleteBook(bookEntity.getId())).isFalse();
    }

    @Test
    public void whenGetBookCoverCorrectID_thenShouldReturnCover() {
        Mockito.when(bookRepository.findById(0L)).thenReturn(Optional.of(bookEntity));
        assertThat(bookService.getBookCover(bookEntity.getId())).isPresent();
    }

    @Test
    public void whenGetBookCoverWrongID_thenShouldReturnEmpty() {
        Mockito.when(bookRepository.findById(0L)).thenReturn(Optional.empty());
        assertThat(bookService.getBookCover(bookEntity.getId())).isEmpty();
    }

    @Test
    public void whenGetBookCoverIDWithException_thenShouldReturnEmpty() {
        bookEntity.setTitle(null);
        Mockito.when(bookRepository.findById(0L)).thenReturn(Optional.of(bookEntity));
        assertThat(bookService.getBookCover(bookEntity.getId())).isEmpty();
    }
}
