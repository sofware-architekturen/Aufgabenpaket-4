package de.hskl.itanalyst.BuchlagerBackendMonolith.service;

import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.AddressEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.AuthorEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.BookEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.PublisherEntity;

import java.util.List;

public interface ISearchService {
    List<AuthorEntity> searchAuthorsForString(final String searchString);

    List<PublisherEntity> searchPublishersForString(final String searchString);

    List<BookEntity> searchBooksForString(final String searchString);

    List<AddressEntity> searchAddressesForString(final String searchString);
}
