package de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Search;

import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Address.AddressDTO;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Author.AuthorDTO;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Book.BookLightDTO;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Publisher.PublisherDTO;

import java.util.Set;

public class MultiSearchResultDTO {
    public Set<AuthorDTO> authors;
    public Set<PublisherDTO> publishers;
    public Set<BookLightDTO> books;
    public Set<AddressDTO> addresses;
}
