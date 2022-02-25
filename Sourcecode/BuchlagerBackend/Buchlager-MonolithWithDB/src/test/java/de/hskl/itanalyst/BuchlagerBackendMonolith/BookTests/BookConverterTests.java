package de.hskl.itanalyst.BuchlagerBackendMonolith.BookTests;

import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Book.BookDTO;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Book.BookLightDTO;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Book.BookUpdateDTO;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.AddressEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.AuthorEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.BookEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.PublisherEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.repository.AuthorRepository;
import de.hskl.itanalyst.BuchlagerBackendMonolith.repository.BookRepository;
import de.hskl.itanalyst.BuchlagerBackendMonolith.repository.PublisherRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class BookConverterTests {
    @SpyBean
    private ModelMapper modelMapper;

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private PublisherRepository publisherRepository;

    @MockBean
    private BookRepository bookRepository;

    private final AuthorEntity authorEntity = new AuthorEntity("Max", "Mustermann", new HashSet<>(1));
    private final AddressEntity addressEntity = new AddressEntity("musterhausen");
    private final PublisherEntity publisherEntity = new PublisherEntity(addressEntity, new HashSet<>(1), "Musterverlag");
    private BookEntity bookEntity;
    private BookEntity bookEntityUpdated;

    @BeforeEach
    public void setUp() {
        bookEntity = new BookEntity(publisherEntity, Set.of(authorEntity), "Mustertitel", 10);
        bookEntityUpdated = new BookEntity(publisherEntity, Set.of(authorEntity), "MustertitelUpdated", 10);

        Mockito.when(authorRepository.findById(0L)).thenReturn(Optional.of(authorEntity));
        Mockito.when(authorRepository.findById(1L)).thenReturn(Optional.empty());
        Mockito.when(bookRepository.findById(0L)).thenReturn(Optional.of(bookEntity));
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(bookEntityUpdated));
        Mockito.when(bookRepository.findById(2L)).thenReturn(Optional.empty());
        Mockito.when(publisherRepository.findById(0L)).thenReturn(Optional.of(publisherEntity));
        Mockito.when(publisherRepository.findById(1L)).thenReturn(Optional.empty());
    }

    @AfterEach
    public void tearDown() {
        Mockito.reset(authorRepository);
        Mockito.reset(modelMapper);
    }

    @Test
    public void contextLoads() throws Exception {
        assertThat(modelMapper).isNotNull();
        assertThat(authorRepository).isNotNull();
        assertThat(bookRepository).isNotNull();
        assertThat(publisherRepository).isNotNull();
    }

    @Test
    public void whenConvertBookEntityToDTO_thenShouldReturnBookDTO() {
        // given

        // when
        BookDTO bookDTO = modelMapper.map(bookEntity, BookDTO.class);

        // then
        assertThat(bookDTO).isNotNull();
        Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(bookEntity, BookDTO.class);
        assertThat(bookDTO.PublisherName).isEqualTo(bookEntity.getPublisher().getName());
        assertThat(bookDTO.PublisherAddressCity).isEqualTo(bookEntity.getPublisher().getAddress().getCity());
        assertThat(bookDTO.authors.size()).isEqualTo(bookEntity.getAuthors().size());
    }

    @Test
    public void whenConvertBookEntityToDTOLight_thenShouldReturnBookLightDTO() {
        // given

        // when
        BookLightDTO bookLightDTO = modelMapper.map(bookEntity, BookLightDTO.class);

        // then
        assertThat(bookLightDTO).isNotNull();
        Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(bookEntity, BookLightDTO.class);
        assertThat(bookLightDTO.title).isEqualTo(bookEntity.getTitle());
        assertThat(bookLightDTO.authors.size()).isEqualTo(bookEntity.getAuthors().size());
        assertThat(bookLightDTO.authors.iterator().next()).isEqualTo(bookEntity.getAuthors().iterator().next().toString());
        assertThat(bookLightDTO.id).isEqualTo(bookEntity.getId());
    }

    @Test
    public void whenConvertBookUpdateDtoFullValidIdToEntity_thenShouldReturnBookEntity() {
        // given
        BookUpdateDTO bookUpdateDTO = new BookUpdateDTO();
        bookUpdateDTO.publisherId = 0L;
        bookUpdateDTO.title = "mm";
        bookUpdateDTO.authorIds = Set.of(0L);
        bookUpdateDTO.id = 0L;

        // when
        BookEntity bookEntityMapped = modelMapper.map(bookUpdateDTO, BookEntity.class);

        // then
        assertThat(bookEntityMapped).isNotNull();
        Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(bookUpdateDTO, BookEntity.class);
        assertThat(bookEntityMapped.getTitle()).isEqualTo(bookUpdateDTO.title);
        assertThat(bookEntityMapped.getPublisher().getName()).isEqualTo(publisherEntity.getName());
        assertThat(bookEntityMapped.getAuthors().size()).isEqualTo(bookEntity.getAuthors().size());
        assertThat(bookEntityMapped.getAuthors().iterator().next().getFirstName()).isEqualTo(bookEntity.getAuthors().iterator().next().getFirstName());
    }

    @Test
    public void whenConvertBookUpdateDtoPartiallyValidIdToEntity_thenShouldReturnBookEntity() {
        // given
        BookUpdateDTO bookUpdateDTO = new BookUpdateDTO();
        bookUpdateDTO.publisherId = null;
        bookUpdateDTO.title = null;
        bookUpdateDTO.authorIds = null;
        bookUpdateDTO.id = 0L;

        // when
        BookEntity bookEntityMapped = modelMapper.map(bookUpdateDTO, BookEntity.class);

        // then
        assertThat(bookEntityMapped).isNotNull();
        Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(bookUpdateDTO, BookEntity.class);
        assertThat(bookEntityMapped.getTitle()).isEqualTo(bookEntity.getTitle());
        assertThat(bookEntityMapped.getPublisher().getName()).isEqualTo(publisherEntity.getName());
        assertThat(bookEntityMapped.getAuthors().size()).isEqualTo(bookEntity.getAuthors().size());
        assertThat(bookEntityMapped.getAuthors().iterator().next().getFirstName()).isEqualTo(bookEntity.getAuthors().iterator().next().getFirstName());

        // given
        bookUpdateDTO = new BookUpdateDTO();
        bookUpdateDTO.publisherId = null;
        bookUpdateDTO.title = "neu";
        bookUpdateDTO.authorIds = null;
        bookUpdateDTO.id = 0L;

        // when
        bookEntityMapped = modelMapper.map(bookUpdateDTO, BookEntity.class);

        // then
        assertThat(bookEntityMapped).isNotNull();
        Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(bookUpdateDTO, BookEntity.class);
        assertThat(bookEntityMapped.getTitle()).isEqualTo(bookUpdateDTO.title);
        assertThat(bookEntityMapped.getPublisher().getName()).isEqualTo(publisherEntity.getName());
        assertThat(bookEntityMapped.getAuthors().size()).isEqualTo(bookEntity.getAuthors().size());
        assertThat(bookEntityMapped.getAuthors().iterator().next().getFirstName()).isEqualTo(bookEntity.getAuthors().iterator().next().getFirstName());

        // given
        bookUpdateDTO = new BookUpdateDTO();
        bookUpdateDTO.publisherId = null;
        bookUpdateDTO.title = "  ";
        bookUpdateDTO.authorIds = null;
        bookUpdateDTO.id = 0L;

        // when
        bookEntityMapped = modelMapper.map(bookUpdateDTO, BookEntity.class);

        // then
        assertThat(bookEntityMapped).isNotNull();
        Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(bookUpdateDTO, BookEntity.class);
        assertThat(bookEntityMapped.getTitle()).isEqualTo(bookEntity.getTitle());
        assertThat(bookEntityMapped.getPublisher().getName()).isEqualTo(publisherEntity.getName());
        assertThat(bookEntityMapped.getAuthors().size()).isEqualTo(bookEntity.getAuthors().size());
        assertThat(bookEntityMapped.getAuthors().iterator().next().getFirstName()).isEqualTo(bookEntity.getAuthors().iterator().next().getFirstName());

        // given
        bookUpdateDTO = new BookUpdateDTO();
        bookUpdateDTO.publisherId = 0L;
        bookUpdateDTO.title = "neu";
        bookUpdateDTO.authorIds = null;
        bookUpdateDTO.id = 0L;

        // when
        bookEntityMapped = modelMapper.map(bookUpdateDTO, BookEntity.class);

        // then
        assertThat(bookEntityMapped).isNotNull();
        Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(bookUpdateDTO, BookEntity.class);
        assertThat(bookEntityMapped.getTitle()).isEqualTo(bookUpdateDTO.title);
        assertThat(bookEntityMapped.getPublisher().getName()).isEqualTo(publisherEntity.getName());
        assertThat(bookEntityMapped.getAuthors().size()).isEqualTo(bookEntity.getAuthors().size());
        assertThat(bookEntityMapped.getAuthors().iterator().next().getFirstName()).isEqualTo(bookEntity.getAuthors().iterator().next().getFirstName());

        // given
        bookUpdateDTO = new BookUpdateDTO();
        bookUpdateDTO.publisherId = 1L;
        bookUpdateDTO.title = "neu";
        bookUpdateDTO.authorIds = null;
        bookUpdateDTO.id = 0L;

        // when
        try {
            bookEntityMapped = modelMapper.map(bookUpdateDTO, BookEntity.class);
            assertThat(true).isFalse(); // should never be reached
        } catch (Exception e) {
            // then
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
        }

        // given
        bookUpdateDTO = new BookUpdateDTO();
        bookUpdateDTO.publisherId = 1L;
        bookUpdateDTO.title = "neu";
        bookUpdateDTO.authorIds = Set.of(1L);
        bookUpdateDTO.id = 0L;

        // when
        try {
            bookEntityMapped = modelMapper.map(bookUpdateDTO, BookEntity.class);
            assertThat(true).isFalse(); // should never be reached
        } catch (Exception e) {
            // then
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
        }

        // given
        bookUpdateDTO = new BookUpdateDTO();
        bookUpdateDTO.publisherId = 0L;
        bookUpdateDTO.title = "neu";
        bookUpdateDTO.authorIds = Set.of(0L);
        bookUpdateDTO.id = 0L;

        // when
        bookEntityMapped = modelMapper.map(bookUpdateDTO, BookEntity.class);

        // then
        assertThat(bookEntityMapped).isNotNull();
        Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(bookUpdateDTO, BookEntity.class);
        assertThat(bookEntityMapped.getTitle()).isEqualTo(bookUpdateDTO.title);
        assertThat(bookEntityMapped.getPublisher().getName()).isEqualTo(publisherEntity.getName());
        assertThat(bookEntityMapped.getAuthors().size()).isEqualTo(bookEntity.getAuthors().size());
        assertThat(bookEntityMapped.getAuthors().iterator().next().getFirstName()).isEqualTo(bookEntity.getAuthors().iterator().next().getFirstName());
    }

    @Test
    public void whenConvertBookUpdateDtoInvalidIdToEntity_thenShouldReturnException() {
        // given
        BookUpdateDTO bookUpdateDTO = new BookUpdateDTO();
        bookUpdateDTO.publisherId = 1L;
        bookUpdateDTO.title = "neu";
        bookUpdateDTO.authorIds = Set.of(1L);
        bookUpdateDTO.id = 2L;

        // when
        try {
            BookEntity bookEntityMapped = modelMapper.map(bookUpdateDTO, BookEntity.class);
            assertThat(true).isFalse(); // should never be reached
        } catch (Exception e) {
            // then
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    public void whenConvertBookUpdateDtoNoIdToEntity_thenShouldReturnBookEntity() {
        // given
        BookUpdateDTO bookUpdateDTO = new BookUpdateDTO();
        bookUpdateDTO.publisherId = null;
        bookUpdateDTO.title = null;
        bookUpdateDTO.authorIds = null;
        bookUpdateDTO.id = null;

        // when
        try {
            BookEntity bookEntityMapped = modelMapper.map(bookUpdateDTO, BookEntity.class);
            assertThat(true).isFalse(); // should never be reached
        } catch (Exception e) {
            // then
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
        }

        // given
        bookUpdateDTO = new BookUpdateDTO();
        bookUpdateDTO.publisherId = null;
        bookUpdateDTO.title = "   ";
        bookUpdateDTO.authorIds = null;
        bookUpdateDTO.id = null;

        // when
        try {
            BookEntity bookEntityMapped = modelMapper.map(bookUpdateDTO, BookEntity.class);
            assertThat(true).isFalse(); // should never be reached
        } catch (Exception e) {
            // then
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
        }

        // given
        bookUpdateDTO = new BookUpdateDTO();
        bookUpdateDTO.publisherId = null;
        bookUpdateDTO.title = "m";
        bookUpdateDTO.authorIds = null;
        bookUpdateDTO.id = null;

        // when
        try {
            BookEntity bookEntityMapped = modelMapper.map(bookUpdateDTO, BookEntity.class);
            assertThat(true).isFalse(); // should never be reached
        } catch (Exception e) {
            // then
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
        }

        // given
        bookUpdateDTO = new BookUpdateDTO();
        bookUpdateDTO.publisherId = 0L;
        bookUpdateDTO.title = "m";
        bookUpdateDTO.authorIds = null;
        bookUpdateDTO.id = null;

        // when
        try {
            BookEntity bookEntityMapped = modelMapper.map(bookUpdateDTO, BookEntity.class);
            assertThat(true).isFalse(); // should never be reached
        } catch (Exception e) {
            // then
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
        }

        // given
        bookUpdateDTO = new BookUpdateDTO();
        bookUpdateDTO.publisherId = 1L;
        bookUpdateDTO.title = "m";
        bookUpdateDTO.authorIds = Set.of(0L);
        bookUpdateDTO.id = null;

        // when
        try {
            BookEntity bookEntityMapped = modelMapper.map(bookUpdateDTO, BookEntity.class);
            assertThat(true).isFalse(); // should never be reached
        } catch (Exception e) {
            // then
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
        }

        // given
        bookUpdateDTO = new BookUpdateDTO();
        bookUpdateDTO.publisherId = 0L;
        bookUpdateDTO.title = "m";
        bookUpdateDTO.authorIds = Set.of(1L);
        bookUpdateDTO.id = null;

        // when
        try {
            BookEntity bookEntityMapped = modelMapper.map(bookUpdateDTO, BookEntity.class);
            assertThat(true).isFalse(); // should never be reached
        } catch (Exception e) {
            // then
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
        }

        // given
        bookUpdateDTO = new BookUpdateDTO();
        bookUpdateDTO.publisherId = 0L;
        bookUpdateDTO.title = "m";
        bookUpdateDTO.authorIds = Set.of(0L);
        bookUpdateDTO.id = null;

        // when
        BookEntity bookEntityMapped = modelMapper.map(bookUpdateDTO, BookEntity.class);

        // then
        assertThat(bookEntityMapped).isNotNull();
        Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(bookUpdateDTO, BookEntity.class);
        assertThat(bookEntityMapped.getTitle()).isEqualTo(bookUpdateDTO.title);
        assertThat(bookEntityMapped.getPublisher().getName()).isEqualTo(publisherEntity.getName());
        assertThat(bookEntityMapped.getAuthors().size()).isEqualTo(bookEntity.getAuthors().size());
        assertThat(bookEntityMapped.getAuthors().iterator().next().getFirstName()).isEqualTo(bookEntity.getAuthors().iterator().next().getFirstName());
    }
}
