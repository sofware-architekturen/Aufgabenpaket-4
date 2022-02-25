package de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Address;

import java.util.Objects;

public class AddressDTO {
    public Long id;
    public String city;

    @Override
    public int hashCode() {
        return Objects.hash(id, city);
    }
}
