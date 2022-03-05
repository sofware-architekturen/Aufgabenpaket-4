package de.hskl.itanalyst.addressauthorcartpublisherservice.AuthorTests;

import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.dto.Author.AuthorDTO;
import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.dto.Author.AuthorUpdateDTO;
import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.model.*;
import de.hskl.itanalyst.addressauthorcartpublisherservice.repository.AuthorRepository;
import de.hskl.itanalyst.addressauthorcartpublisherservice.repository.BookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AuthorConverterTests {
    @SpyBean
    private ModelMapper modelMapper;

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private BookRepository bookRepository;

    private final AuthorEntity authorEntity = new AuthorEntity("Max", "Mustermann", new HashSet<>(1));
    private final AddressEntity addressEntity = new AddressEntity("musterhausen");
    private final PublisherEntity publisherEntity = new PublisherEntity(addressEntity, new HashSet<>(1), "Musterverlag");
    private BookEntity bookEntity;
    private BookEntity bookEntityUpdated;
    private final CartItemEntity cartItemEntity = new CartItemEntity(1, bookEntity);
    private final CartEntity cartEntity = new CartEntity("0", LocalDateTime.now().plus(1, ChronoUnit.MINUTES), Set.of(cartItemEntity));

    @BeforeEach
    public void setUp() {
        bookEntity = new BookEntity(publisherEntity, Set.of(authorEntity), "Mustertitel", 10);
        bookEntityUpdated = new BookEntity(publisherEntity, Set.of(authorEntity), "MustertitelUpdated", 10);

        Mockito.when(authorRepository.findById(0L)).thenReturn(Optional.of(authorEntity));
        Mockito.when(authorRepository.findById(1L)).thenReturn(Optional.empty());
        Mockito.when(bookRepository.findById(0L)).thenReturn(Optional.of(bookEntity));
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(bookEntityUpdated));
        Mockito.when(bookRepository.findById(2L)).thenReturn(Optional.empty());
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
    }

    @Test
    public void whenConvertAuthorEntityToDTO_thenShouldReturnAuthorDTO() {
        // given

        // when
        AuthorDTO authorDTO = modelMapper.map(authorEntity, AuthorDTO.class);

        // then
        assertThat(authorDTO).isNotNull();
        Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(authorEntity, AuthorDTO.class);
        assertThat(authorDTO.firstName).isEqualTo(authorEntity.getFirstName());
        assertThat(authorDTO.books.size()).isEqualTo(authorEntity.getBooks().size()); // should be enough
    }

    @Test
    public void whenConvertAuthorUpdateDtoFullValidIdToEntity_thenShouldReturnAuthorEntity() {
        // given
        AuthorUpdateDTO authorUpdateDTO = new AuthorUpdateDTO();
        authorUpdateDTO.firstName = "mm";
        authorUpdateDTO.lastName = "mm";
        authorUpdateDTO.bookIds = Set.of(0L);
        authorUpdateDTO.id = 0L;

        // when
        AuthorEntity authorEntityMapped = modelMapper.map(authorUpdateDTO, AuthorEntity.class);

        // then
        assertThat(authorEntityMapped).isNotNull();
        Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(authorUpdateDTO, AuthorEntity.class);
        assertThat(authorEntityMapped.getFirstName()).isEqualTo(authorEntity.getFirstName());
        assertThat(authorEntityMapped.getLastName()).isEqualTo(authorEntity.getLastName());
        assertThat(authorEntityMapped.getFirstName()).isEqualTo(authorUpdateDTO.firstName);
        assertThat(authorEntityMapped.getLastName()).isEqualTo(authorUpdateDTO.lastName);
        assertThat(authorEntityMapped.getBooks().iterator().next()).isEqualTo(bookEntity);
    }

    @Test
    public void whenConvertAuthorUpdateDtoPartiallyValidIdToEntity_thenShouldReturnAuthorEntity() {
        // given
        AuthorUpdateDTO authorUpdateDTO = new AuthorUpdateDTO();
        authorUpdateDTO.firstName = null;
        authorUpdateDTO.lastName = null;
        authorUpdateDTO.bookIds = null;
        authorUpdateDTO.id = 0L;

        // when
        AuthorEntity authorEntityMapped = modelMapper.map(authorUpdateDTO, AuthorEntity.class);

        // then
        assertThat(authorEntityMapped).isNotNull();
        Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(authorUpdateDTO, AuthorEntity.class);
        assertThat(authorEntityMapped.getFirstName()).isEqualTo(authorEntity.getFirstName());
        assertThat(authorEntityMapped.getLastName()).isEqualTo(authorEntity.getLastName());
        assertThat(authorEntityMapped.getBooks()).contains(bookEntity, bookEntityUpdated);

        // given
        authorUpdateDTO = new AuthorUpdateDTO();
        authorUpdateDTO.firstName = "mm";
        authorUpdateDTO.lastName = null;
        authorUpdateDTO.bookIds = null;
        authorUpdateDTO.id = 0L;

        // when
        authorEntityMapped = modelMapper.map(authorUpdateDTO, AuthorEntity.class);

        // then
        assertThat(authorEntityMapped).isNotNull();
        Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(authorUpdateDTO, AuthorEntity.class);
        assertThat(authorEntityMapped.getFirstName()).isEqualTo(authorEntity.getFirstName());
        assertThat(authorEntityMapped.getFirstName()).isEqualTo(authorUpdateDTO.firstName);
        assertThat(authorEntityMapped.getLastName()).isEqualTo(authorEntity.getLastName());
        assertThat(authorEntityMapped.getBooks()).contains(bookEntity, bookEntityUpdated);

        // given
        authorUpdateDTO = new AuthorUpdateDTO();
        authorUpdateDTO.firstName = "mm";
        authorUpdateDTO.lastName = "mma";
        authorUpdateDTO.bookIds = null;
        authorUpdateDTO.id = 0L;

        // when
        authorEntityMapped = modelMapper.map(authorUpdateDTO, AuthorEntity.class);

        // then
        assertThat(authorEntityMapped).isNotNull();
        Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(authorUpdateDTO, AuthorEntity.class);
        assertThat(authorEntityMapped.getFirstName()).isEqualTo(authorEntity.getFirstName());
        assertThat(authorEntityMapped.getFirstName()).isEqualTo(authorUpdateDTO.firstName);
        assertThat(authorEntityMapped.getLastName()).isEqualTo(authorEntity.getLastName());
        assertThat(authorEntityMapped.getLastName()).isEqualTo(authorUpdateDTO.lastName);
        assertThat(authorEntityMapped.getBooks()).contains(bookEntity, bookEntityUpdated);

        // given
        authorUpdateDTO = new AuthorUpdateDTO();
        authorUpdateDTO.firstName = "mm";
        authorUpdateDTO.lastName = "mma";
        authorUpdateDTO.bookIds = Set.of(1L);
        authorUpdateDTO.id = 0L;

        // when
        authorEntityMapped = modelMapper.map(authorUpdateDTO, AuthorEntity.class);

        // then
        assertThat(authorEntityMapped).isNotNull();
        Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(authorUpdateDTO, AuthorEntity.class);
        assertThat(authorEntityMapped.getFirstName()).isEqualTo(authorEntity.getFirstName());
        assertThat(authorEntityMapped.getFirstName()).isEqualTo(authorUpdateDTO.firstName);
        assertThat(authorEntityMapped.getLastName()).isEqualTo(authorEntity.getLastName());
        assertThat(authorEntityMapped.getLastName()).isEqualTo(authorUpdateDTO.lastName);
        assertThat(authorEntityMapped.getBooks().iterator().next()).isEqualTo(bookEntityUpdated);
        assertThat(authorEntityMapped.getBooks().iterator().next().getTitle()).isEqualTo(bookEntityUpdated.getTitle());
        assertThat(authorEntityMapped.getBooks().iterator().next().getTitle()).isNotEqualTo(bookEntity.getTitle());

        // given
        authorUpdateDTO = new AuthorUpdateDTO();
        authorUpdateDTO.firstName = "mm";
        authorUpdateDTO.lastName = "mma";
        authorUpdateDTO.bookIds = Set.of(2L);
        authorUpdateDTO.id = 0L;

        // when
        try {
            authorEntityMapped = modelMapper.map(authorUpdateDTO, AuthorEntity.class);
            assertThat(true).isFalse(); // should never be reached
        } catch (Exception e) {
            // then
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    public void whenConvertAuthorUpdateDtoInvalidIdToEntity_thenShouldReturnException() {
        // given
        AuthorUpdateDTO authorUpdateDTO = new AuthorUpdateDTO();
        authorUpdateDTO.firstName = null;
        authorUpdateDTO.lastName = null;
        authorUpdateDTO.bookIds = null;
        authorUpdateDTO.id = 1L;

        // when
        try {
            AuthorEntity authorEntityMapped = modelMapper.map(authorUpdateDTO, AuthorEntity.class);
            assertThat(true).isFalse(); // should never be reached
        } catch (Exception e) {
            // then
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    public void whenConvertAddressUpdateDtoNoIdToEntity_thenShouldReturnAuthorEntity() {
        // given
        AuthorUpdateDTO authorUpdateDTO = new AuthorUpdateDTO();
        authorUpdateDTO.firstName = null;
        authorUpdateDTO.lastName = null;
        authorUpdateDTO.bookIds = null;
        authorUpdateDTO.id = null;

        // when
        try {
            AuthorEntity authorEntityMapped = modelMapper.map(authorUpdateDTO, AuthorEntity.class);
            assertThat(true).isFalse(); // should never be reached
        } catch (Exception e) {
            // then
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
        }

        // given
        authorUpdateDTO = new AuthorUpdateDTO();
        authorUpdateDTO.firstName = "  ";
        authorUpdateDTO.lastName = null;
        authorUpdateDTO.bookIds = null;
        authorUpdateDTO.id = null;

        // when
        try {
            AuthorEntity authorEntityMapped = modelMapper.map(authorUpdateDTO, AuthorEntity.class);
            assertThat(true).isFalse(); // should never be reached
        } catch (Exception e) {
            // then
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
        }

        // given
        authorUpdateDTO = new AuthorUpdateDTO();
        authorUpdateDTO.firstName = "a";
        authorUpdateDTO.lastName = null;
        authorUpdateDTO.bookIds = null;
        authorUpdateDTO.id = null;

        // when
        try {
            AuthorEntity authorEntityMapped = modelMapper.map(authorUpdateDTO, AuthorEntity.class);
            assertThat(true).isFalse(); // should never be reached
        } catch (Exception e) {
            // then
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
        }

        // given
        authorUpdateDTO = new AuthorUpdateDTO();
        authorUpdateDTO.firstName = "a";
        authorUpdateDTO.lastName = "   ";
        authorUpdateDTO.bookIds = null;
        authorUpdateDTO.id = null;

        // when
        try {
            AuthorEntity authorEntityMapped = modelMapper.map(authorUpdateDTO, AuthorEntity.class);
            assertThat(true).isFalse(); // should never be reached
        } catch (Exception e) {
            // then
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
        }

        // given
        authorUpdateDTO = new AuthorUpdateDTO();
        authorUpdateDTO.firstName = "a";
        authorUpdateDTO.lastName = "a";
        authorUpdateDTO.bookIds = Set.of(2L);
        authorUpdateDTO.id = null;

        // when
        try {
            AuthorEntity authorEntityMapped = modelMapper.map(authorUpdateDTO, AuthorEntity.class);
            assertThat(true).isFalse(); // should never be reached
        } catch (Exception e) {
            // then
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
        }

        // given
        authorUpdateDTO = new AuthorUpdateDTO();
        authorUpdateDTO.firstName = "a";
        authorUpdateDTO.lastName = "a";
        authorUpdateDTO.bookIds = Set.of(0L);
        authorUpdateDTO.id = null;

        // when
        AuthorEntity authorEntityMapped = modelMapper.map(authorUpdateDTO, AuthorEntity.class);

        // then
        assertThat(authorEntityMapped).isNotNull();
        Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(authorUpdateDTO, AuthorEntity.class);
        assertThat(authorEntityMapped.getFirstName()).isEqualTo(authorUpdateDTO.firstName);
        assertThat(authorEntityMapped.getLastName()).isEqualTo(authorUpdateDTO.lastName);
        assertThat(authorEntityMapped.getBooks().iterator().next()).isEqualTo(bookEntity);
    }
}
