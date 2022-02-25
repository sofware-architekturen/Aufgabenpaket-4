package de.hskl.itanalyst.BuchlagerBackendMonolith.service;

import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.PublisherEntity;

import java.util.Optional;
import java.util.stream.Stream;

public interface IPublisherService {
    Optional<PublisherEntity> getPublisherById(final long publisherId);

    PublisherEntity addOrUpdatePublisher(final PublisherEntity updatedPublisherEntity);

    boolean deletePublisher(long publisherId);

    Stream<PublisherEntity> getAllPublisherAsStream();
}
