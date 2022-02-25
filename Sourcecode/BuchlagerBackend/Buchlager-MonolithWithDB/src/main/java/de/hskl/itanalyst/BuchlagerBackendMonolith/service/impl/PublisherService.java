package de.hskl.itanalyst.BuchlagerBackendMonolith.service.impl;

import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.PublisherEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.repository.PublisherRepository;
import de.hskl.itanalyst.BuchlagerBackendMonolith.service.IPublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class PublisherService implements IPublisherService {
    @Autowired
    private PublisherRepository publisherRepository;

    public Optional<PublisherEntity> getPublisherById(final long publisherId) {
        final Optional<PublisherEntity> publisherEntity = publisherRepository.findById(publisherId);
        return publisherEntity;
    }

    public PublisherEntity addOrUpdatePublisher(final PublisherEntity updatedPublisherEntity) {
        // All checks are done within the converter

        return publisherRepository.save(updatedPublisherEntity);
    }

    public boolean deletePublisher(long publisherId) {
        final Optional<PublisherEntity> publisherEntity = publisherRepository.findById(publisherId);
        try {
            if (publisherEntity.isPresent()) {
                publisherRepository.delete(publisherEntity.get());
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public Stream<PublisherEntity> getAllPublisherAsStream() {
        final Iterable<PublisherEntity> allPublisher = publisherRepository.findAll();
        return StreamSupport.stream(allPublisher.spliterator(), false);
    }
}
