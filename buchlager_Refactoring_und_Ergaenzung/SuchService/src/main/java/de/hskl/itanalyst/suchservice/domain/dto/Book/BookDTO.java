package de.hskl.itanalyst.suchservice.domain.dto.Book;

import java.util.Set;

public class BookDTO {
    public Long id;
    public String PublisherName;
    public String PublisherAddressCity;
    public Set<String> authors;
    public String title;
    public Long amountInStock;
}
