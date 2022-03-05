package de.hskl.itanalyst.addressauthorcartpublisherservice.service;


import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.model.AddressEntity;

import java.util.Optional;
import java.util.stream.Stream;

public interface IAddressService {
    Optional<AddressEntity> getAddressById(final long addressId);

    AddressEntity addOrUpdateAddress(final AddressEntity updatedAddressEntity);

    boolean deleteAddress(long addressId);

    Stream<AddressEntity> getAllAddressesAsStream();
}
