package de.hskl.itanalyst.addressauthorcartpublisherservice.controller.api;

import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.dto.Address.AddressDTO;
import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.dto.Address.AddressUpdateDTO;
import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.model.AddressEntity;
import de.hskl.itanalyst.addressauthorcartpublisherservice.service.IAddressService;
import io.swagger.annotations.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping(path = "/api/addresses")
@Api(value = "Address API - Verwalten aller Addressen")
public class AddressAPI {
    @Autowired
    private IAddressService addressService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping(value = "/{addressId}")
    @ApiOperation(value = "Anzeigen einer Adresse mit einer bestimmten ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Adresse erfolgreich geliefert.", response = AddressDTO.class),
            @ApiResponse(code = 404, message = "Keine Adresse mit dieser ID vorhanden.")
    })
    public ResponseEntity<AddressDTO> addressById(@ApiParam(value = "Address ID", required = true) @PathVariable("addressId") final long addressId) {
        final Optional<AddressEntity> responseEntity = addressService.getAddressById(addressId);
        return responseEntity.map(addressEntity -> ResponseEntity.ok(modelMapper.map(addressEntity, AddressDTO.class))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @ApiOperation(value = "Alle verfügbaren Adressen anzeigen.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Liste erfolgreich geliefert.", responseContainer = "List", response = AddressDTO.class),
            @ApiResponse(code = 204, message = "Keine Adressen vorhanden.")
    })
    public ResponseEntity<List<AddressDTO>> allAddresses() {
        final Stream<AddressEntity> addressEntityStreamHelper = addressService.getAllAddressesAsStream();
        final List<AddressDTO> responseList = addressEntityStreamHelper.map(this::streamConvertHelper).collect(Collectors.toList());

        if (responseList.size() > 0) {
            return ResponseEntity.ok(responseList);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PutMapping
    @ApiOperation(value = "Hinzufügen oder aktualisieren einer Adresse.", response = AddressDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Adresse erfolgreich aktualisiert.", response = AddressDTO.class),
            @ApiResponse(code = 422, message = "Adresse konnte aufgrund fehlerhafter Eingabedaten nicht aktualisiert werden.")
    })/*
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Adressen ID (nur bei Update notwendig)", required = false, dataType = "Long", dataTypeClass = Long.class, paramType = "query")
    })*/
    public ResponseEntity<AddressDTO> addOrUpdateAddress(@ApiParam(value = "Neue oder aktualisierte Adresse. Adressen ID nur bei Update notwendig.", required = true) @RequestBody final AddressUpdateDTO updatedAddress) {
        try {
            final AddressEntity addressEntity = modelMapper.map(updatedAddress, AddressEntity.class);
            return ResponseEntity.ok(modelMapper.map(addressService.addOrUpdateAddress(addressEntity), AddressDTO.class));
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    @DeleteMapping(value = "/{addressId}")
    @ApiOperation(value = "Löschen einer Adresse mit einer bestimmten ID.", response = Boolean.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Adresse erfolgreich gelöscht."),
            @ApiResponse(code = 404, message = "Keine Adresse mit dieser ID vorhanden.")
    })
    public ResponseEntity deleteAddress(@ApiParam(value = "Address ID", required = true) @PathVariable("addressId") final long addressId) {
        if (addressService.deleteAddress(addressId)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Helper methods

    @ApiIgnore
    private AddressDTO streamConvertHelper(final AddressEntity addressEntity) {
        return modelMapper.map(addressEntity, AddressDTO.class);
    }
}
