package de.hskl.itanalyst.addressauthorcartpublisherservice.AddressTests;

import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.model.AddressEntity;
import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.model.PublisherEntity;
import de.hskl.itanalyst.addressauthorcartpublisherservice.repository.AddressRepository;
import de.hskl.itanalyst.addressauthorcartpublisherservice.repository.PublisherRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class AddressPersistenceTests {
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PublisherRepository publisherRepository;
    @Autowired
    private EntityManager em;

    @Test
    public void contextLoads() throws Exception {
        assertThat(addressRepository).isNotNull();
        assertThat(publisherRepository).isNotNull();
        assertThat(em).isNotNull();
    }

    @AfterEach
    public void afterEach() {
        // delete all entries
        addressRepository.deleteAll();
    }

    @Test
    public void whenDeleteAddress_thenLinkedPublisherisDeleted() {
        // given
        AddressEntity addressEntity = new AddressEntity("mustercity");
        addressEntity = addressRepository.save(addressEntity);
        PublisherEntity publisherEntity = new PublisherEntity(addressEntity, new HashSet<>(), "demo");
        publisherEntity = publisherRepository.save(publisherEntity);

        // when
        addressEntity = addressRepository.findById(addressEntity.getId()).get(); // refresh
        addressRepository.delete(addressEntity);

        Optional<List<PublisherEntity>> foundPublishers = publisherRepository.findByNameContainingIgnoreCase("demo");
        assertThat(foundPublishers).isEmpty();
    }

    @Test
    public void whenDeletePublisher_thenAddressisNOTDeleted() {
        // given
        AddressEntity addressEntity = new AddressEntity("mustercity");
        addressEntity = addressRepository.save(addressEntity);
        PublisherEntity publisherEntity = new PublisherEntity(addressEntity, new HashSet<>(), "demo");
        publisherEntity = publisherRepository.save(publisherEntity);

        // when
        publisherRepository.delete(publisherEntity);
        Optional<AddressEntity> tmpAddressEntity = addressRepository.findById(addressEntity.getId()); // refresh

        Optional<List<PublisherEntity>> foundPublishers = publisherRepository.findByNameContainingIgnoreCase("demo");
        assertThat(foundPublishers).isEmpty();

        assertThat(tmpAddressEntity).isPresent();
        assertThat(tmpAddressEntity.get().getCity()).isEqualTo(addressEntity.getCity());
    }

    @Test
    public void whenFindById_thenReturnAddress() {
        // given
        AddressEntity addressEntity = new AddressEntity("mustercity");
        addressRepository.save(addressEntity);

        // when
        Optional<AddressEntity> foundRepo = addressRepository.findById(addressEntity.getId());

        // then
        assertThat(foundRepo).isPresent();
        if (foundRepo.isPresent()) {
            assertThat(foundRepo.get().getCity()).isEqualTo(addressEntity.getCity());
        }
    }

    @Test
    public void whenFindByCityContainingIgnoreCase_thenReturnOneAddress() {
        // given
        AddressEntity addressEntity = new AddressEntity("mustercity");
        addressRepository.save(addressEntity);

        // when (given lower case only)
        Optional<List<AddressEntity>> foundOne = addressRepository.findByCityContainingIgnoreCase("mustercity");

        // then
        assertThat(foundOne).isPresent();
        if (foundOne.isPresent()) {
            assertThat(foundOne.get().size()).isEqualTo(1);
            assertThat(foundOne.get().iterator().next().getCity()).isEqualTo(addressEntity.getCity());
        }

        // when (given upper/lower case)
        foundOne = addressRepository.findByCityContainingIgnoreCase("MuSteRCitY");

        // then
        assertThat(foundOne).isPresent();
        if (foundOne.isPresent()) {
            assertThat(foundOne.get().size()).isEqualTo(1);
            assertThat(foundOne.get().iterator().next().getCity()).isEqualTo(addressEntity.getCity());
        }
    }

    @Test
    public void whenFindByCityContainingIgnoreCase_thenReturnTwoAddresses() {
        // given
        AddressEntity addressEntity = new AddressEntity("mustercity");
        addressRepository.save(addressEntity);
        addressEntity = new AddressEntity("mustercitya");
        addressRepository.save(addressEntity);
        final String referenceCityname = "mustercity";

        // when (given lower case only)
        Optional<List<AddressEntity>> foundTwo = addressRepository.findByCityContainingIgnoreCase(referenceCityname);

        // then
        assertThat(foundTwo).isPresent();
        if (foundTwo.isPresent()) {
            assertThat(foundTwo.get().size()).isEqualTo(2);
            foundTwo.get().forEach(entry -> {
                assertThat(entry.getCity().contains(referenceCityname)).isTrue();
            });
        }
    }
}
