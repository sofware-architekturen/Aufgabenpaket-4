package de.hskl.itanalyst.suchservice.service;


import de.hskl.itanalyst.suchservice.domain.model.AddressEntity;
import de.hskl.itanalyst.suchservice.domain.model.AuthorEntity;
import de.hskl.itanalyst.suchservice.domain.model.BookEntity;
import de.hskl.itanalyst.suchservice.domain.model.PublisherEntity;

import java.util.List;

public interface ISearchService {
    List<AuthorEntity> searchAuthorsForString(final String searchString);

    List<PublisherEntity> searchPublishersForString(final String searchString);

    List<BookEntity> searchBooksForString(final String searchString);

    List<AddressEntity> searchAddressesForString(final String searchString);
}
