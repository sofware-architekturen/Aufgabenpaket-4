package de.hskl.itanalyst.BuchlagerBackendMonolith.PublisherTests;

import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.AddressEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.PublisherEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.repository.PublisherRepository;
import de.hskl.itanalyst.BuchlagerBackendMonolith.service.IPublisherService;
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
public class PublisherServiceTests {
    @Autowired
    private IPublisherService publisherService;

    @MockBean
    private PublisherRepository publisherRepository;

    private final AddressEntity addressEntity = new AddressEntity("musterhausen");
    private final AddressEntity addressEntityUpdated = new AddressEntity("musterhausenUpdated");
    private final PublisherEntity publisherEntity = new PublisherEntity(addressEntity, Collections.EMPTY_SET, "Musterverlag");
    private final PublisherEntity publisherEntityUpdatedName = new PublisherEntity(addressEntity, Collections.EMPTY_SET, "MusterverlagUpdated");

    @BeforeEach
    public void setUp() {
        Mockito.when(publisherRepository.findById(0L)).thenReturn(Optional.of(publisherEntity));
        Spliterator<PublisherEntity> spliterator = Arrays.spliterator(new PublisherEntity[]{publisherEntity});
        Iterable<PublisherEntity> publisherEntityIterable = new Iterable<PublisherEntity>() {
            @Override
            public Spliterator<PublisherEntity> spliterator() {
                return spliterator;
            }

            @Override
            public Iterator<PublisherEntity> iterator() {
                return new Iterator<PublisherEntity>() {
                    private boolean hasNext = true;

                    @Override
                    public boolean hasNext() {
                        return hasNext;
                    }

                    @Override
                    public PublisherEntity next() {
                        try {
                            return publisherEntity;
                        } finally {
                            hasNext = false;
                        }
                    }
                };
            }
        };

        Mockito.when(publisherRepository.findAll()).thenReturn(publisherEntityIterable);
    }

    @AfterEach
    public void afterEach() {
        // reset mock
        Mockito.reset(publisherRepository);
    }

    @Test
    public void whenValidId_thenPublisherShouldBeFound() {
        Optional<PublisherEntity> foundService = publisherService.getPublisherById(0L);

        assertThat(foundService).isPresent();
        foundService.ifPresent(entity -> assertThat(entity.getName()).isEqualTo(publisherEntity.getName()));
    }

    @Test
    public void whenGetAllPublisher_thenPublisherCountShouldBeGreater0() {
        Stream<PublisherEntity> foundStream = publisherService.getAllPublisherAsStream();
        assertThat(foundStream).isNotNull();
    }

    /*
        @Test
        public void whenAddOrUpdatePublisherWrongName_thenShouldReturnException() {
            PublisherEntity publisherEntityWrong = new PublisherEntity(addressEntity, Collections.EMPTY_SET, "    ");
            try {
                publisherService.addOrUpdatePublisher(publisherEntityWrong);
                assertThat(true).isFalse(); // Should not happen!
            } catch (IllegalArgumentException e) {
                assertThat(e.getMessage()).isEqualTo("Publisher has an empty name.");
            }
        }
    */
    @Test
    public void whenAddOrUpdatePublisherAddPublisher_thenShouldReturnPublisher() {
        Mockito.when(publisherRepository.save(publisherEntity)).thenReturn(publisherEntity);
        assertThat(publisherService.addOrUpdatePublisher(publisherEntity)).isEqualTo(publisherEntity);
    }

    @Test
    public void whenAddOrUpdatePublisherUpdatePublisher_thenShouldReturnUpdatedPublisher() {
        Mockito.when(publisherRepository.save(publisherEntityUpdatedName)).thenReturn(publisherEntityUpdatedName);
        assertThat(publisherService.addOrUpdatePublisher(publisherEntityUpdatedName)).isEqualTo(publisherEntityUpdatedName);
        Mockito.verify(publisherRepository, VerificationModeFactory.atLeast(1)).save(publisherEntityUpdatedName);
    }

    @Test
    public void whenDeletePublisher_thenShouldReturnTrueIfSuccess() {
        Mockito.when(publisherRepository.findById(0L)).thenReturn(Optional.of(publisherEntity));
        assertThat(publisherService.deletePublisher(publisherEntity.getId())).isTrue();
        Mockito.verify(publisherRepository, VerificationModeFactory.atLeast(1)).delete(publisherEntity);
    }

    @Test
    public void whenDeletePublisher_thenShouldReturnFalseIfFailed() {
        Mockito.when(publisherRepository.findById(0L)).thenReturn(Optional.empty());
        assertThat(publisherService.deletePublisher(publisherEntity.getId())).isFalse();
    }

    @Test
    public void whenDeletePublisher_thenShouldReturnExceptionIfDBFailed() {
        Mockito.doThrow(new IllegalArgumentException("whenDeletePublisher_thenShouldReturnExceptionIfDBFailed")).when(publisherRepository).delete(publisherEntity);
        assertThat(publisherService.deletePublisher(publisherEntity.getId())).isFalse();
    }
}
