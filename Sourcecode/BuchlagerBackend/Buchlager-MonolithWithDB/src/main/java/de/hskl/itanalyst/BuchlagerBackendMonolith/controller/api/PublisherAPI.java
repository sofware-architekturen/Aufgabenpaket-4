package de.hskl.itanalyst.BuchlagerBackendMonolith.controller.api;

import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Publisher.PublisherDTO;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Publisher.PublisherUpdateDTO;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.PublisherEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.service.IPublisherService;
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
@RequestMapping(path = "/api/publisher")
@Api(value = "Publisher API - Verwalten aller Verlage")
public class PublisherAPI {
    @Autowired
    private IPublisherService publisherService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping(value = "/{publisherId}")
    @ApiOperation(value = "Anzeigen eines Verlags mit einer bestimmten ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Verlag erfolgreich geliefert.", response = PublisherDTO.class),
            @ApiResponse(code = 404, message = "Kein Verlag mit dieser ID vorhanden.")
    })
    public ResponseEntity<PublisherDTO> publisherById(@ApiParam(value = "Publisher ID", required = true) @PathVariable("publisherId") final long publisherId) {
        final Optional<PublisherEntity> publisherEntity = publisherService.getPublisherById(publisherId);
        return publisherEntity.map(pubEntity -> ResponseEntity.ok(modelMapper.map(pubEntity, PublisherDTO.class))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @ApiOperation(value = "Alle verfügbaren Verlage anzeigen.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Liste erfolgreich geliefert.", responseContainer = "List", response = PublisherDTO.class),
            @ApiResponse(code = 204, message = "Keine Verlage vorhanden.")
    })
    public ResponseEntity<List<PublisherDTO>> allPublisher() {
        final Stream<PublisherEntity> authorsEntityStreamHelper = publisherService.getAllPublisherAsStream();
        final List<PublisherDTO> responseList = authorsEntityStreamHelper.map(this::streamConvertHelper).collect(Collectors.toList());

        if (!responseList.isEmpty()) {
            return ResponseEntity.ok(responseList);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PutMapping
    @ApiOperation(value = "Hinzufügen oder aktualisieren eines Verlags.", response = PublisherDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Verlag erfolgreich aktualisiert.", response = PublisherDTO.class),
            @ApiResponse(code = 422, message = "Verlag konnte aufgrund fehlerhafter Eingabedaten nicht aktualisiert werden.")
    })/*
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Verlags ID (nur bei Update notwendig)", required = false, dataType = "Long", dataTypeClass = Long.class, paramType = "query")
    })*/
    public ResponseEntity<PublisherDTO> addOrUpdatePublisher(@ApiParam(value = "Neuer oder aktualisierter Verlag. Verlags ID nur bei Update notwendig.", required = true) @RequestBody final PublisherUpdateDTO updatedPublisher) {
        try {
            final PublisherEntity publisherEntity = modelMapper.map(updatedPublisher, PublisherEntity.class);
            return ResponseEntity.ok(modelMapper.map(publisherService.addOrUpdatePublisher(publisherEntity), PublisherDTO.class));
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    @DeleteMapping(value = "/{publisherId}")
    @ApiOperation(value = "Löschen eines Verlags mit einer bestimmten ID. Löschen des Verlags führt automatisch auch zur Löschung aller Bücher des Verlags.", response = Boolean.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Verlag erfolgreich gelöscht.", response = Boolean.class),
            @ApiResponse(code = 404, message = "Keinen Verlag mit dieser ID vorhanden.")
    })
    public ResponseEntity<Boolean> deletePublisher(@ApiParam(value = "Publisher ID", required = true) @PathVariable("publisherId") final long publisherId) {
        if (publisherService.deletePublisher(publisherId)) {
            return ResponseEntity.ok(Boolean.TRUE);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Helper methods

    @ApiIgnore
    private PublisherDTO streamConvertHelper(final PublisherEntity publisherEntity) {
        return modelMapper.map(publisherEntity, PublisherDTO.class);
    }
}
