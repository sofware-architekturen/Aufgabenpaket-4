package de.hskl.itanalyst.suchservice.domain.dto.Address;

import java.util.Objects;

public class AddressUpdateDTO {
    public Long id;
    public String city;

    @Override
    public int hashCode() {
        return Objects.hash(id, city);
    }
}
