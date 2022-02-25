package de.hskl.itanalyst.BuchlagerBackendMonolith.service.impl;

import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.AddressEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.AuthorEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.BookEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.PublisherEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.repository.AddressRepository;
import de.hskl.itanalyst.BuchlagerBackendMonolith.repository.AuthorRepository;
import de.hskl.itanalyst.BuchlagerBackendMonolith.repository.BookRepository;
import de.hskl.itanalyst.BuchlagerBackendMonolith.repository.PublisherRepository;
import de.hskl.itanalyst.BuchlagerBackendMonolith.service.ISearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SearchService implements ISearchService {
    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PublisherRepository publisherRepository;


    public List<AuthorEntity> searchAuthorsForString(final String searchString) {
        final Optional<List<AuthorEntity>> result = authorRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(searchString, searchString);
        return result.orElseGet(ArrayList::new);
    }

    public List<PublisherEntity> searchPublishersForString(final String searchString) {
        final Optional<List<PublisherEntity>> result = publisherRepository.findByNameContainingIgnoreCase(searchString);
        return result.orElseGet(ArrayList::new);
    }

    public List<BookEntity> searchBooksForString(final String searchString) {
        final Optional<List<BookEntity>> result = bookRepository.findByTitleContainingIgnoreCase(searchString);
        return result.orElseGet(ArrayList::new);
    }

    public List<AddressEntity> searchAddressesForString(final String searchString) {
        final Optional<List<AddressEntity>> result = addressRepository.findByCityContainingIgnoreCase(searchString);
        return result.orElseGet(ArrayList::new);
    }
}
