package de.hskl.itanalyst.BuchlagerBackendMonolith.AddressTests;

import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Address.AddressDTO;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Address.AddressUpdateDTO;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.AddressEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.repository.AddressRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AddressConverterTests {
    @SpyBean
    private ModelMapper modelMapper;

    @MockBean
    private AddressRepository addressRepository;

    private final AddressEntity addressEntityInRepo = new AddressEntity("musterhausen");

    @BeforeEach
    public void setUp() {
        Mockito.when(addressRepository.findById(0L)).thenReturn(Optional.of(addressEntityInRepo));
        Mockito.when(addressRepository.findById(1L)).thenReturn(Optional.empty());
    }

    @AfterEach
    public void tearDown() {
        Mockito.reset(addressRepository);
        Mockito.reset(modelMapper);
    }

    @Test
    public void contextLoads() throws Exception {
        assertThat(modelMapper).isNotNull();
    }

    @Test
    public void whenConvertAddressEntityToDTO_thenShouldReturnAddressDTO() {
        // given
        AddressEntity addressEntity = new AddressEntity("mustercity");

        // when
        AddressDTO addressDTO = modelMapper.map(addressEntity, AddressDTO.class);

        // then
        assertThat(addressDTO).isNotNull();
        Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(addressEntity, AddressDTO.class);
        assertThat(addressDTO.city).isEqualTo(addressEntity.getCity());
        assertThat(addressDTO.id).isEqualTo(addressEntity.getId());
    }

    @Test
    public void whenConvertAddressUpdateDtoValidIdToEntity_thenShouldReturnAddressEntity() {
        // given
        AddressUpdateDTO addressUpdateDTO = new AddressUpdateDTO();
        addressUpdateDTO.city = "mm";
        addressUpdateDTO.id = 0L;

        // when
        AddressEntity addressEntity = modelMapper.map(addressUpdateDTO, AddressEntity.class);

        // then
        assertThat(addressEntity).isNotNull();
        Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(addressUpdateDTO, AddressEntity.class);
        assertThat(addressUpdateDTO.city).isEqualTo(addressEntity.getCity());
        assertThat(addressUpdateDTO.id).isEqualTo(addressEntity.getId());
    }

    @Test
    public void whenConvertAddressUpdateDtoValidIdInvalidCityToEntity_thenShouldReturnAddressEntity() {
        // given
        AddressUpdateDTO addressUpdateDTO = new AddressUpdateDTO();
        addressUpdateDTO.city = "  ";
        addressUpdateDTO.id = 0L;

        // when
        try {
            AddressEntity addressEntity = modelMapper.map(addressUpdateDTO, AddressEntity.class);
            assertThat(true).isFalse(); // should never happen
        } catch (Exception e) {
            // then
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
            assertThat(e.getCause().getMessage()).isEqualTo("city value is invalid");
            Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(addressUpdateDTO, AddressEntity.class);
        }
    }

    @Test
    public void whenConvertAddressUpdateDtoInvalidIdToEntity_thenShouldReturnAddressEntity() {
        // given
        AddressUpdateDTO addressUpdateDTO = new AddressUpdateDTO();
        addressUpdateDTO.city = "mm";
        addressUpdateDTO.id = 1L;

        // when
        try {
            AddressEntity addressEntity = modelMapper.map(addressUpdateDTO, AddressEntity.class);
            assertThat(true).isFalse(); // should never happen
        } catch (Exception e) {
            // then
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
            assertThat(e.getCause().getMessage()).isEqualTo("id is invalid");
            Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(addressUpdateDTO, AddressEntity.class);
        }
    }

    @Test
    public void whenConvertAddressUpdateDtoNoIdToEntity_thenShouldReturnAddressEntity() {
        // given
        AddressUpdateDTO addressUpdateDTO = new AddressUpdateDTO();
        addressUpdateDTO.city = "mm";
        addressUpdateDTO.id = null;

        // when
        AddressEntity addressEntity = modelMapper.map(addressUpdateDTO, AddressEntity.class);

        // then
        assertThat(addressEntity).isNotNull();
        Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(addressUpdateDTO, AddressEntity.class);
        assertThat(addressUpdateDTO.city).isEqualTo(addressEntity.getCity());
    }

    @Test
    public void whenConvertAddressUpdateDtoInvalidCityToEntity_thenShouldReturnAddressEntity() {
        // given
        AddressUpdateDTO addressUpdateDTO = new AddressUpdateDTO();
        addressUpdateDTO.city = "  ";
        addressUpdateDTO.id = null;

        // when
        try {
            AddressEntity addressEntity = modelMapper.map(addressUpdateDTO, AddressEntity.class);
            assertThat(true).isFalse(); // should never happen
        } catch (Exception e) {
            // then
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
            assertThat(e.getCause().getMessage()).isEqualTo("city value is invalid");
            Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(addressUpdateDTO, AddressEntity.class);
        }
    }
}
