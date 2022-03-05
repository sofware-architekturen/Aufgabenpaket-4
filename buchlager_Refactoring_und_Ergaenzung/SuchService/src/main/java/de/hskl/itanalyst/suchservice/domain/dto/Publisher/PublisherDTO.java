package de.hskl.itanalyst.suchservice.domain.dto.Publisher;

import de.hskl.itanalyst.suchservice.domain.dto.Book.BookLightDTO;

import java.util.Objects;
import java.util.Set;

public class PublisherDTO {
    public Long id;
    public String city;
    public Set<BookLightDTO> books;
    public String name;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PublisherDTO that = (PublisherDTO) o;
        return id.equals(that.id) &&
                city.equals(that.city) &&
                name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, city, name);
    }
}
