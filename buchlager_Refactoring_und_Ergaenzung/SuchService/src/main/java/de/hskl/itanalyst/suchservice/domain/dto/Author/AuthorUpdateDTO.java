package de.hskl.itanalyst.suchservice.domain.dto.Author;

import java.util.Set;

public class AuthorUpdateDTO {
    public Long id;
    public String firstName;
    public String lastName;
    public Set<Long> bookIds;
}
