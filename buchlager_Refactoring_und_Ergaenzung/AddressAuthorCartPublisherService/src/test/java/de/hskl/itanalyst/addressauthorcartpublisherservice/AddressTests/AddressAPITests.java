package de.hskl.itanalyst.addressauthorcartpublisherservice.AddressTests;

import com.fasterxml.jackson.databind.ObjectMapper;;
import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.dto.Address.AddressDTO;
import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.dto.Address.AddressUpdateDTO;
import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.model.AddressEntity;
import de.hskl.itanalyst.addressauthorcartpublisherservice.repository.AddressRepository;
import de.hskl.itanalyst.addressauthorcartpublisherservice.service.IAddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.stream.StreamSupport;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AddressAPITests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IAddressService addressService;

    @MockBean
    private AddressRepository addressRepository;

    @SpyBean
    private ModelMapper modelMapper;

    private AddressEntity addressEntity;
    private AddressDTO addressDTO;
    private AddressUpdateDTO addressUpdateDTO;

    @BeforeEach
    public void setUp() {
        Mockito.reset(modelMapper);
        Mockito.when(addressService.getAddressById(0)).thenReturn(Optional.of(addressEntity));
        Mockito.when(addressService.getAddressById(1)).thenReturn(Optional.empty());
        Mockito.when(addressService.deleteAddress(0)).thenReturn(true);
        Mockito.when(addressService.deleteAddress(1)).thenReturn(false);
        Mockito.when(addressRepository.findById(0L)).thenReturn(Optional.of(addressEntity));
        Mockito.when(addressRepository.findById(1L)).thenReturn(Optional.empty());

        addressUpdateDTO = new AddressUpdateDTO();
        addressUpdateDTO.city = "newcity";
        addressUpdateDTO.id = 0L;
    }

    @PostConstruct
    public void init() {
        addressEntity = new AddressEntity("mustercity");
        addressDTO = modelMapper.map(addressEntity, AddressDTO.class);
    }

    @Test
    public void getAPIAddressByID0_ShouldReturnAddressObject() throws Exception {
        this.mockMvc.perform(get("/api/addresses/0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(new ObjectMapper().writeValueAsString(addressDTO)));
    }

    @Test
    public void getAPIAddressByID1_ShouldReturnAddressNotFound() throws Exception {
        this.mockMvc.perform(get("/api/addresses/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    public void getAPIallAddressesExisting_ShouldReturnAddress() throws Exception {
        Spliterator<AddressEntity> spliterator = Arrays.spliterator(new AddressEntity[]{addressEntity});
        Mockito.when(addressService.getAllAddressesAsStream()).thenReturn(StreamSupport.stream(spliterator, false));

        this.mockMvc.perform(get("/api/addresses"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(List.of(addressDTO))));
    }

    @Test
    public void getAPIallAddressesNoAddressesExistent_ShouldReturnAddress() throws Exception {
        Spliterator<AddressEntity> spliterator = Arrays.spliterator(new AddressEntity[]{});
        Mockito.when(addressService.getAllAddressesAsStream()).thenReturn(StreamSupport.stream(spliterator, false));

        this.mockMvc.perform(get("/api/addresses"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    public void putAPIAddress_ShouldReturnAddress() throws Exception {
        addressEntity.setCity(addressUpdateDTO.city);
        Mockito.when(addressService.addOrUpdateAddress(Mockito.any())).thenReturn(addressEntity);

        this.mockMvc.perform(put("/api/addresses").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(addressUpdateDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(addressUpdateDTO)));

        Mockito.verify(modelMapper, VerificationModeFactory.times(2)).map(Mockito.any(), Mockito.any());
    }

    @Test
    public void putAPIWrongAddressObject_ShouldReturnStatusUnprocessableEntity() throws Exception {
        Mockito.when(addressService.addOrUpdateAddress(Mockito.any())).thenThrow(new RuntimeException("testexcption"));
        addressUpdateDTO.id = 1L;

        this.mockMvc.perform(put("/api/addresses").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(addressUpdateDTO)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(""));

        Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(Mockito.any(), Mockito.any());
    }

    @Test
    public void deleteAPIAddressObject_ShouldReturnTrue() throws Exception {
        this.mockMvc.perform(delete("/api/addresses/0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    public void deleteAPIWrongAddressObject_ShouldReturnTrue() throws Exception {
        this.mockMvc.perform(delete("/api/addresses/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }
}
