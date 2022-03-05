package de.hskl.itanalyst.addressauthorcartpublisherservice.domain.dto.Book;

import java.util.Objects;
import java.util.Set;

public class BookLightDTO {
    public Long id;
    public Set<String> authors;
    public String title;
    public Long amountInStock;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BookLightDTO that = (BookLightDTO) o;
        return id.equals(that.id) &&
                title.equals(that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }
}
