package de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Publisher;

import java.util.Set;

public class PublisherUpdateDTO {
    public Long id;
    public Long addressId;
    public Set<Long> bookIds;
    public String name;
}
