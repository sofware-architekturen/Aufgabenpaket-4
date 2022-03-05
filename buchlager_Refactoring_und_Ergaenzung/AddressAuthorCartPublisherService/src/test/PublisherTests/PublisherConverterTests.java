package de.hskl.itanalyst.BuchlagerBackendMonolith.PublisherTests;

import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Publisher.PublisherDTO;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Publisher.PublisherUpdateDTO;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.AddressEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.AuthorEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.BookEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.PublisherEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.repository.BookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PublisherConverterTests {
    @SpyBean
    private ModelMapper modelMapper;

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private PublisherRepository publisherRepository;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private AddressRepository addressRepository;

    private AuthorEntity authorEntity;
    private AddressEntity addressEntity;
    private PublisherEntity publisherEntity;
    private BookEntity bookEntity;

    @BeforeEach
    public void setUp() {
        authorEntity = new AuthorEntity("Max", "Mustermann", Collections.EMPTY_SET);
        addressEntity = new AddressEntity("musterhausen");
        publisherEntity = new PublisherEntity(addressEntity, new HashSet<>(1), "Musterverlag");
        bookEntity = new BookEntity(publisherEntity, Set.of(authorEntity), "Mustertitel", 10);

        Mockito.when(authorRepository.findById(0L)).thenReturn(Optional.of(authorEntity));
        Mockito.when(authorRepository.findById(1L)).thenReturn(Optional.empty());
        Mockito.when(bookRepository.findById(0L)).thenReturn(Optional.of(bookEntity));
        Mockito.when(bookRepository.findById(2L)).thenReturn(Optional.empty());
        Mockito.when(publisherRepository.findById(0L)).thenReturn(Optional.of(publisherEntity));
        Mockito.when(publisherRepository.findById(1L)).thenReturn(Optional.empty());
        Mockito.when(addressRepository.findById(0L)).thenReturn(Optional.of(addressEntity));
        Mockito.when(addressRepository.findById(1L)).thenReturn(Optional.empty());
    }

    @AfterEach
    public void tearDown() {
        Mockito.reset(authorRepository);
        Mockito.reset(publisherRepository);
        Mockito.reset(addressRepository);
        Mockito.reset(bookRepository);
        Mockito.reset(modelMapper);
    }

    @Test
    public void contextLoads() throws Exception {
        assertThat(modelMapper).isNotNull();
        assertThat(authorRepository).isNotNull();
        assertThat(bookRepository).isNotNull();
        assertThat(publisherRepository).isNotNull();
        assertThat(addressRepository).isNotNull();
    }

    @Test
    public void whenConvertPublisherEntityToDTO_thenShouldReturnPublisherDTO() {
        // given

        // when
        PublisherDTO publisherDTO = modelMapper.map(publisherEntity, PublisherDTO.class);

        // then
        assertThat(publisherDTO).isNotNull();
        Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(publisherEntity, PublisherDTO.class);
        assertThat(publisherDTO.name).isEqualTo(publisherEntity.getName());
        assertThat(publisherDTO.city).isEqualTo(publisherEntity.getAddress().getCity());
        assertThat(publisherDTO.books.size()).isEqualTo(publisherEntity.getBooks().size());
        assertThat(publisherDTO.books.iterator().next().title).isEqualTo(publisherEntity.getBooks().iterator().next().getTitle());
    }

    @Test
    public void whenConvertPublisherUpdateDtoFullValidIdToEntity_thenShouldReturnPublisherEntity() {
        // given
        PublisherUpdateDTO publisherUpdateDTO = new PublisherUpdateDTO();
        publisherUpdateDTO.name = "neu";
        publisherUpdateDTO.bookIds = Set.of(0L);
        publisherUpdateDTO.addressId = 0L;
        publisherUpdateDTO.id = 0L;

        // when
        PublisherEntity publisherEntityMapped = modelMapper.map(publisherUpdateDTO, PublisherEntity.class);

        // then
        assertThat(publisherEntityMapped).isNotNull();
        Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(publisherUpdateDTO, PublisherEntity.class);
        assertThat(publisherEntityMapped.getName()).isEqualTo(publisherUpdateDTO.name);
        assertThat(publisherEntityMapped.getAddress()).isEqualTo(addressEntity);
        assertThat(publisherEntityMapped.getBooks().size()).isEqualTo(publisherEntity.getBooks().size());
        assertThat(publisherEntityMapped.getBooks().iterator().next().getTitle()).isEqualTo(publisherEntity.getBooks().iterator().next().getTitle());
    }

    @Test
    public void whenConvertPublisherUpdateDtoPartiallyValidIdToEntity_thenShouldReturnPublisherEntity() {
        // given
        PublisherUpdateDTO publisherUpdateDTO = new PublisherUpdateDTO();
        publisherUpdateDTO.name = null;
        publisherUpdateDTO.bookIds = null;
        publisherUpdateDTO.addressId = null;
        publisherUpdateDTO.id = 0L;

        // when
        PublisherEntity publisherEntityMapped = modelMapper.map(publisherUpdateDTO, PublisherEntity.class);

        // then
        assertThat(publisherEntityMapped).isNotNull();
        Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(publisherUpdateDTO, PublisherEntity.class);
        assertThat(publisherEntityMapped.getName()).isEqualTo(publisherEntity.getName());
        assertThat(publisherEntityMapped.getAddress()).isEqualTo(addressEntity);
        assertThat(publisherEntityMapped.getBooks().size()).isEqualTo(publisherEntity.getBooks().size());
        assertThat(publisherEntityMapped.getBooks().iterator().next().getTitle()).isEqualTo(publisherEntity.getBooks().iterator().next().getTitle());

        // given
        publisherUpdateDTO = new PublisherUpdateDTO();
        publisherUpdateDTO.name = "neuer";
        publisherUpdateDTO.bookIds = null;
        publisherUpdateDTO.addressId = null;
        publisherUpdateDTO.id = 0L;

        // when
        publisherEntityMapped = modelMapper.map(publisherUpdateDTO, PublisherEntity.class);

        // then
        assertThat(publisherEntityMapped).isNotNull();
        Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(publisherUpdateDTO, PublisherEntity.class);
        assertThat(publisherEntityMapped.getName()).isEqualTo(publisherUpdateDTO.name);
        assertThat(publisherEntityMapped.getAddress()).isEqualTo(addressEntity);
        assertThat(publisherEntityMapped.getBooks().size()).isEqualTo(publisherEntity.getBooks().size());
        assertThat(publisherEntityMapped.getBooks().iterator().next().getTitle()).isEqualTo(publisherEntity.getBooks().iterator().next().getTitle());

        // given
        publisherUpdateDTO = new PublisherUpdateDTO();
        publisherUpdateDTO.name = "   ";
        publisherUpdateDTO.bookIds = null;
        publisherUpdateDTO.addressId = null;
        publisherUpdateDTO.id = 0L;

        // when
        publisherEntityMapped = modelMapper.map(publisherUpdateDTO, PublisherEntity.class);

        // then
        assertThat(publisherEntityMapped).isNotNull();
        Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(publisherUpdateDTO, PublisherEntity.class);
        assertThat(publisherEntityMapped.getName()).isNotEqualTo(publisherUpdateDTO.name);
        assertThat(publisherEntityMapped.getName()).isEqualTo(publisherEntity.getName());
        assertThat(publisherEntityMapped.getAddress()).isEqualTo(addressEntity);
        assertThat(publisherEntityMapped.getBooks().size()).isEqualTo(publisherEntity.getBooks().size());
        assertThat(publisherEntityMapped.getBooks().iterator().next().getTitle()).isEqualTo(publisherEntity.getBooks().iterator().next().getTitle());

        // given
        publisherUpdateDTO = new PublisherUpdateDTO();
        publisherUpdateDTO.name = "neuer";
        publisherUpdateDTO.bookIds = null;
        publisherUpdateDTO.addressId = 0L;
        publisherUpdateDTO.id = 0L;

        // when
        publisherEntityMapped = modelMapper.map(publisherUpdateDTO, PublisherEntity.class);

        // then
        assertThat(publisherEntityMapped).isNotNull();
        Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(publisherUpdateDTO, PublisherEntity.class);
        assertThat(publisherEntityMapped.getName()).isEqualTo(publisherUpdateDTO.name);
        assertThat(publisherEntityMapped.getAddress()).isEqualTo(addressEntity);
        assertThat(publisherEntityMapped.getBooks().size()).isEqualTo(publisherEntity.getBooks().size());
        assertThat(publisherEntityMapped.getBooks().iterator().next().getTitle()).isEqualTo(publisherEntity.getBooks().iterator().next().getTitle());

        // given
        publisherUpdateDTO = new PublisherUpdateDTO();
        publisherUpdateDTO.name = "neuer";
        publisherUpdateDTO.bookIds = Set.of(0L);
        publisherUpdateDTO.addressId = 0L;
        publisherUpdateDTO.id = 0L;

        // when
        publisherEntityMapped = modelMapper.map(publisherUpdateDTO, PublisherEntity.class);

        // then
        assertThat(publisherEntityMapped).isNotNull();
        Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(publisherUpdateDTO, PublisherEntity.class);
        assertThat(publisherEntityMapped.getName()).isEqualTo(publisherUpdateDTO.name);
        assertThat(publisherEntityMapped.getAddress()).isEqualTo(addressEntity);
        assertThat(publisherEntityMapped.getBooks().size()).isEqualTo(publisherEntity.getBooks().size());
        assertThat(publisherEntityMapped.getBooks().iterator().next().getTitle()).isEqualTo(publisherEntity.getBooks().iterator().next().getTitle());


        // given
        publisherUpdateDTO = new PublisherUpdateDTO();
        publisherUpdateDTO.name = "neuer";
        publisherUpdateDTO.bookIds = Set.of(2L);
        publisherUpdateDTO.addressId = 0L;
        publisherUpdateDTO.id = 0L;

        // when
        try {
            publisherEntityMapped = modelMapper.map(publisherUpdateDTO, PublisherEntity.class);
            assertThat(true).isFalse(); // should never be reached
        } catch (Exception e) {
            // then
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
        }

        // given
        publisherUpdateDTO = new PublisherUpdateDTO();
        publisherUpdateDTO.name = "neuer";
        publisherUpdateDTO.bookIds = Set.of(0L);
        publisherUpdateDTO.addressId = 1L;
        publisherUpdateDTO.id = 0L;

        // when
        try {
            publisherEntityMapped = modelMapper.map(publisherUpdateDTO, PublisherEntity.class);
            assertThat(true).isFalse(); // should never be reached
        } catch (Exception e) {
            // then
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    public void whenConvertPublisherUpdateDtoInvalidIdToEntity_thenShouldReturnException() {
        // given
        PublisherUpdateDTO publisherUpdateDTO = new PublisherUpdateDTO();
        publisherUpdateDTO.name = "neuer";
        publisherUpdateDTO.bookIds = Set.of(0L);
        publisherUpdateDTO.addressId = 0L;
        publisherUpdateDTO.id = 1L;

        // when
        try {
            PublisherEntity publisherEntityMapped = modelMapper.map(publisherUpdateDTO, PublisherEntity.class);
            assertThat(true).isFalse(); // should never be reached
        } catch (Exception e) {
            // then
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    public void whenConvertPublisherUpdateDtoNoIdToEntity_thenShouldReturnPublisherEntity() {
        // given
        PublisherUpdateDTO publisherUpdateDTO = new PublisherUpdateDTO();
        publisherUpdateDTO.name = null;
        publisherUpdateDTO.bookIds = null;
        publisherUpdateDTO.addressId = null;
        publisherUpdateDTO.id = null;

        // when
        try {
            PublisherEntity publisherEntityMapped = modelMapper.map(publisherUpdateDTO, PublisherEntity.class);
            assertThat(true).isFalse(); // should never be reached
        } catch (Exception e) {
            // then
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
        }

        // given
        publisherUpdateDTO = new PublisherUpdateDTO();
        publisherUpdateDTO.name = null;
        publisherUpdateDTO.bookIds = null;
        publisherUpdateDTO.addressId = 0L;
        publisherUpdateDTO.id = null;

        // when
        try {
            PublisherEntity publisherEntityMapped = modelMapper.map(publisherUpdateDTO, PublisherEntity.class);
            assertThat(true).isFalse(); // should never be reached
        } catch (Exception e) {
            // then
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
        }

        // given
        publisherUpdateDTO = new PublisherUpdateDTO();
        publisherUpdateDTO.name = "   ";
        publisherUpdateDTO.bookIds = null;
        publisherUpdateDTO.addressId = 0L;
        publisherUpdateDTO.id = null;

        // when
        try {
            PublisherEntity publisherEntityMapped = modelMapper.map(publisherUpdateDTO, PublisherEntity.class);
            assertThat(true).isFalse(); // should never be reached
        } catch (Exception e) {
            // then
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
        }

        // given
        publisherUpdateDTO = new PublisherUpdateDTO();
        publisherUpdateDTO.name = "neu";
        publisherUpdateDTO.bookIds = null;
        publisherUpdateDTO.addressId = 1L;
        publisherUpdateDTO.id = null;

        // when
        try {
            PublisherEntity publisherEntityMapped = modelMapper.map(publisherUpdateDTO, PublisherEntity.class);
            assertThat(true).isFalse(); // should never be reached
        } catch (Exception e) {
            // then
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
        }

        // given
        publisherUpdateDTO = new PublisherUpdateDTO();
        publisherUpdateDTO.name = "neu";
        publisherUpdateDTO.bookIds = Set.of(2L);
        publisherUpdateDTO.addressId = 1L;
        publisherUpdateDTO.id = null;

        // when
        try {
            PublisherEntity publisherEntityMapped = modelMapper.map(publisherUpdateDTO, PublisherEntity.class);
            assertThat(true).isFalse(); // should never be reached
        } catch (Exception e) {
            // then
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
        }

        // given
        publisherUpdateDTO = new PublisherUpdateDTO();
        publisherUpdateDTO.name = "neu";
        publisherUpdateDTO.bookIds = Set.of(0L);
        publisherUpdateDTO.addressId = 0L;
        publisherUpdateDTO.id = null;

        // when
        PublisherEntity publisherEntityMapped = modelMapper.map(publisherUpdateDTO, PublisherEntity.class);

        // then
        assertThat(publisherEntityMapped).isNotNull();
        Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(publisherUpdateDTO, PublisherEntity.class);
        assertThat(publisherEntityMapped.getName()).isEqualTo(publisherUpdateDTO.name);
        assertThat(publisherEntityMapped.getAddress().getCity()).isEqualTo(publisherEntity.getAddress().getCity());
        assertThat(publisherEntityMapped.getBooks().size()).isEqualTo(publisherUpdateDTO.bookIds.size());
        assertThat(publisherEntityMapped.getBooks().iterator().next().getTitle()).isEqualTo(bookEntity.getTitle());
    }
}
