package de.hskl.itanalyst.BuchlagerBackendMonolith.controller.api;

import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Address.AddressDTO;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Author.AuthorDTO;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Book.BookLightDTO;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Publisher.PublisherDTO;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Search.MultiSearchResultDTO;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.AddressEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.AuthorEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.BookEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.PublisherEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.service.ISearchService;
import de.hskl.itanalyst.BuchlagerBackendMonolith.utils.ModelMapperUtils;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping(path = "/api/search")
@Api(value = "Search API - Suche innerhalb des Buchlagers")
public class SearchAPI {
    @Autowired
    private ISearchService searchService;

    @Autowired
    private ModelMapperUtils modelMapperUtils;

    @GetMapping(value = "/all/{searchParameter}")
    @ApiOperation(value = "Anzeigen von Büchern, Verlagen, Autoren oder Adressen passend zum Suchparameter.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Gefundene Ergebnisse.", response = MultiSearchResultDTO.class),
            @ApiResponse(code = 404, message = "Kein Suchergebnis vorhanden.")
    })
    public ResponseEntity<MultiSearchResultDTO> searchMultiProperty(@ApiParam(value = "Search parameter", required = true) @PathVariable("searchParameter") final String searchString) {
        try {
            final List<AuthorDTO> authors = modelMapperUtils.mapAll(searchService.searchAuthorsForString(searchString), AuthorDTO.class);
            final List<PublisherDTO> publishers = modelMapperUtils.mapAll(searchService.searchPublishersForString(searchString), PublisherDTO.class);
            final List<BookLightDTO> books = modelMapperUtils.mapAll(searchService.searchBooksForString(searchString), BookLightDTO.class);
            final List<AddressDTO> addresses = modelMapperUtils.mapAll(searchService.searchAddressesForString(searchString), AddressDTO.class);

            if ((authors.size() + publishers.size() + books.size() + addresses.size()) == 0) {
                return ResponseEntity.notFound().build();
            }

            final MultiSearchResultDTO multiSearchResultDTO = new MultiSearchResultDTO();
            multiSearchResultDTO.addresses = new HashSet<>(addresses);
            multiSearchResultDTO.authors = new HashSet<>(authors);
            multiSearchResultDTO.books = new HashSet<>(books);
            multiSearchResultDTO.publishers = new HashSet<>(publishers);

            return ResponseEntity.ok(multiSearchResultDTO);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/books/{searchParameter}")
    @ApiOperation(value = "Anzeigen von Büchern passend zum Suchparameter.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Gefundene Bücher.", responseContainer = "List", response = BookLightDTO.class),
            @ApiResponse(code = 404, message = "Kein Suchergebnis vorhanden.")
    })
    public ResponseEntity<List<BookLightDTO>> searchBookForProperty(@ApiParam(value = "Search parameter", required = true) @PathVariable("searchParameter") final String searchString) {
        final List<BookEntity> books = searchService.searchBooksForString(searchString);

        if (books.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(modelMapperUtils.mapAll(books, BookLightDTO.class));
    }

    @GetMapping(value = "/publishers/{searchParameter}")
    @ApiOperation(value = "Anzeigen von Verlagen passend zum Suchparameter.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Gefundene Verlage.", responseContainer = "List", response = PublisherDTO.class),
            @ApiResponse(code = 404, message = "Kein Suchergebnis vorhanden.")
    })
    public ResponseEntity<List<PublisherDTO>> searchPublisherForProperty(@ApiParam(value = "Search parameter", required = true) @PathVariable("searchParameter") final String searchString) {
        final List<PublisherEntity> publishers = searchService.searchPublishersForString(searchString);

        if (publishers.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(modelMapperUtils.mapAll(publishers, PublisherDTO.class));
    }

    @GetMapping(value = "/authors/{searchParameter}")
    @ApiOperation(value = "Anzeigen von Autoren passend zum Suchparameter.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Gefundene Autoren.", responseContainer = "List", response = AuthorDTO.class),
            @ApiResponse(code = 404, message = "Kein Suchergebnis vorhanden.")
    })
    public ResponseEntity<List<AuthorDTO>> searchAuthorsForProperty(@ApiParam(value = "Search parameter", required = true) @PathVariable("searchParameter") final String searchString) {
        final List<AuthorEntity> authors = searchService.searchAuthorsForString(searchString);

        if (authors.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(modelMapperUtils.mapAll(authors, AuthorDTO.class));
    }

    @GetMapping(value = "/addresses/{searchParameter}")
    @ApiOperation(value = "Anzeigen von Adressen passend zum Suchparameter.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Gefundene Adressen.", responseContainer = "List", response = AddressDTO.class),
            @ApiResponse(code = 404, message = "Kein Suchergebnis vorhanden.")
    })
    public ResponseEntity<List<AddressDTO>> searchAddressForProperty(@ApiParam(value = "Search parameter", required = true) @PathVariable("searchParameter") final String searchString) {
        final List<AddressEntity> addresses = searchService.searchAddressesForString(searchString);

        if (addresses.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(modelMapperUtils.mapAll(addresses, AddressDTO.class));
    }
}
