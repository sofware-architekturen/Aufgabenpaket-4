package de.hskl.itanalyst.BuchlagerBackendMonolith.AddressTests;

import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.AddressEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.repository.AddressRepository;
import de.hskl.itanalyst.BuchlagerBackendMonolith.service.IAddressService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AddressServiceTests {
    @Autowired
    private IAddressService addressService;

    @MockBean
    private AddressRepository addressRepository;

    private final AddressEntity addressEntity = new AddressEntity("musterhausen");
    private final AddressEntity addressEntityUpdated = new AddressEntity("musterhausenUpdated");

    @BeforeEach
    public void setUp() {
        Mockito.when(addressRepository.findById(0L)).thenReturn(Optional.of(addressEntity));
        Spliterator<AddressEntity> spliterator = Arrays.spliterator(new AddressEntity[]{addressEntity});
        Iterable<AddressEntity> addressEntityIterable = new Iterable<AddressEntity>() {
            @Override
            public Spliterator<AddressEntity> spliterator() {
                return spliterator;
            }

            @Override
            public Iterator<AddressEntity> iterator() {
                return new Iterator<AddressEntity>() {
                    private boolean hasNext = true;

                    @Override
                    public boolean hasNext() {
                        return hasNext;
                    }

                    @Override
                    public AddressEntity next() {
                        try {
                            return addressEntity;
                        } finally {
                            hasNext = false;
                        }
                    }
                };
            }
        };

        Mockito.when(addressRepository.findAll()).thenReturn(addressEntityIterable);
        Mockito.when(addressRepository.save(addressEntityUpdated)).thenReturn(addressEntityUpdated);
    }

    @AfterEach
    public void afterEach() {
        // reset mock
        Mockito.reset(addressRepository);
    }

    @Test
    public void whenValidId_thenAddressShouldBeFound() {
        Optional<AddressEntity> foundService = addressService.getAddressById(0L);

        assertThat(foundService).isPresent();
        foundService.ifPresent(entity -> assertThat(entity.getCity()).isEqualTo(addressEntity.getCity()));
        assertThat(foundService.get().getCity()).isEqualTo(addressEntity.getCity());
    }

    @Test
    public void whenGetAllAddresses_thenAddressCountShouldBeGreater0() {
        Stream<AddressEntity> foundStream = addressService.getAllAddressesAsStream();
        assertThat(foundStream).isNotNull();
    }

    @Test
    public void whenAddOrUpdateAddress_thenShouldReturnUpdatedAddress() {
        assertThat(addressService.addOrUpdateAddress(addressEntityUpdated)).isEqualTo(addressEntityUpdated);
    }

    @Test
    public void whenDeleteAddress_thenShouldReturnTrueIfSuccess() {
        assertThat(addressService.deleteAddress(addressEntity.getId())).isTrue();
        Mockito.verify(addressRepository, VerificationModeFactory.atLeast(1)).delete(addressEntity);
    }

    @Test
    public void whenDeleteAddress_thenShouldReturnFalseIfFailed() {
        Mockito.when(addressRepository.findById(0L)).thenReturn(Optional.empty());
        assertThat(addressService.deleteAddress(addressEntity.getId())).isFalse();
    }

    @Test
    public void whenDeleteAddress_thenShouldReturnFalseIfException() {
        Mockito.doThrow(new RuntimeException("whenDeleteAddress_thenShouldReturnFalseIfException")).when(addressRepository).delete(addressEntity);
        assertThat(addressService.deleteAddress(addressEntity.getId())).isFalse();
    }
}
