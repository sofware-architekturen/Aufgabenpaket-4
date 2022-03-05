package de.hskl.itanalyst.suchservice.domain.converter;

import de.hskl.itanalyst.suchservice.domain.dto.Address.AddressDTO;
import de.hskl.itanalyst.suchservice.domain.dto.Address.AddressUpdateDTO;
import de.hskl.itanalyst.suchservice.domain.model.AddressEntity;
import de.hskl.itanalyst.suchservice.repository.AddressRepository;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Component
public class AddressConverters {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AddressRepository addressRepository;

    @PostConstruct
    public void postConstruct() {
        modelMapper.addConverter(addressEntityAddressDTOConverter);
        modelMapper.addConverter(addressUpdateDTOAddressEntityConverter);
    }

    private final Converter<AddressEntity, AddressDTO> addressEntityAddressDTOConverter = new AbstractConverter<AddressEntity, AddressDTO>() {
        @Override
        protected AddressDTO convert(AddressEntity addressEntity) {
            final AddressDTO addressDTO = new AddressDTO();
            addressDTO.city = addressEntity.getCity();
            addressDTO.id = addressEntity.getId();
            return addressDTO;
        }
    };

    private final Converter<AddressUpdateDTO, AddressEntity> addressUpdateDTOAddressEntityConverter = new AbstractConverter<AddressUpdateDTO, AddressEntity>() {
        @Override
        protected AddressEntity convert(AddressUpdateDTO addressUpdateDTO) {
            if (null != addressUpdateDTO.id) { // change address
                if (null == addressUpdateDTO.city || addressUpdateDTO.city.trim().isBlank()) {
                    throw new IllegalArgumentException("city value is invalid");
                }

                final Optional<AddressEntity> addressEntity = addressRepository.findById(addressUpdateDTO.id);
                addressEntity.ifPresentOrElse(entity -> {
                    entity.setCity(addressUpdateDTO.city);
                }, () -> {
                    throw new IllegalArgumentException("id is invalid");
                });

                return addressEntity.get();
            } else { // new address
                if (null == addressUpdateDTO.city || addressUpdateDTO.city.trim().isBlank()) {
                    throw new IllegalArgumentException("city value is invalid");
                }

                return new AddressEntity(addressUpdateDTO.city);
            }
        }
    };
}
