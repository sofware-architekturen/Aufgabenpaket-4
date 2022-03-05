package de.hskl.itanalyst.addressauthorcartpublisherservice.domain.dto.Book;

import java.util.Set;

public class BookUpdateDTO {
    public Long id;
    public Long publisherId;
    public Set<Long> authorIds;
    public String title;
    public Long amountInStock;
}
