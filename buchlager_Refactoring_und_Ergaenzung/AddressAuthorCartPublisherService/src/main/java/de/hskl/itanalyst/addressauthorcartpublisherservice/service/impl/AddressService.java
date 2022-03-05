package de.hskl.itanalyst.addressauthorcartpublisherservice.service.impl;


import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.model.AddressEntity;
import de.hskl.itanalyst.addressauthorcartpublisherservice.repository.AddressRepository;
import de.hskl.itanalyst.addressauthorcartpublisherservice.service.IAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class AddressService implements IAddressService {
    @Autowired
    private AddressRepository addressRepository;

    public Optional<AddressEntity> getAddressById(final long addressId) {
        final Optional<AddressEntity> addressEntity = addressRepository.findById(addressId);
        return addressEntity;
    }

    public AddressEntity addOrUpdateAddress(final AddressEntity updatedAddressEntity) {
        return addressRepository.save(updatedAddressEntity);
    }

    public boolean deleteAddress(long addressId) {
        final Optional<AddressEntity> addressEntity = addressRepository.findById(addressId);
        try {
            if (addressEntity.isPresent()) {
                addressRepository.delete(addressEntity.get());
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public Stream<AddressEntity> getAllAddressesAsStream() {
        final Iterable<AddressEntity> allAddresses = addressRepository.findAll();
        return StreamSupport.stream(allAddresses.spliterator(), false);
    }
}
