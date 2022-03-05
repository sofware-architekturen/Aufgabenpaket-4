package de.hskl.itanalyst.suchservice.domain.dto.Author;


import de.hskl.itanalyst.suchservice.domain.dto.Book.BookLightDTO;

import java.util.Set;

public class AuthorDTO {
    public Long id;
    public String firstName;
    public String lastName;
    public Set<BookLightDTO> books;
}
