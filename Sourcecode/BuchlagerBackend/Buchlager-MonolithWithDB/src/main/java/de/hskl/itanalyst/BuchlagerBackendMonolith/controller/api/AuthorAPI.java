package de.hskl.itanalyst.BuchlagerBackendMonolith.controller.api;

import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Author.AuthorDTO;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Author.AuthorUpdateDTO;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.AuthorEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.service.IAuthorService;
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
@RequestMapping(path = "/api/authors")
@Api(value = "Author API - Verwalten aller Autoren")
public class AuthorAPI {
    @Autowired
    private IAuthorService authorService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping(value = "/{authorId}")
    @ApiOperation(value = "Anzeigen eines Autors mit einer bestimmten ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Autor erfolgreich geliefert.", response = AuthorDTO.class),
            @ApiResponse(code = 404, message = "Kein Autor mit dieser ID vorhanden.")
    })
    public ResponseEntity<AuthorDTO> authorById(@ApiParam(value = "Author ID", required = true) @PathVariable("authorId") final long authorId) {
        final Optional<AuthorEntity> responseEntity = authorService.getAuthorById(authorId);
        return responseEntity.map(authorEntity -> ResponseEntity.ok(modelMapper.map(authorEntity, AuthorDTO.class))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @ApiOperation(value = "Alle verfügbaren Adressen anzeigen.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Liste erfolgreich geliefert.", responseContainer = "List", response = AuthorDTO.class),
            @ApiResponse(code = 204, message = "Keine Adressen vorhanden.")
    })
    public ResponseEntity<List<AuthorDTO>> allAuthors() {
        final Stream<AuthorEntity> authorsEntityStreamHelper = authorService.getAllAuthorsAsStream();
        final List<AuthorDTO> responseList = authorsEntityStreamHelper.map(this::streamConvertHelper).collect(Collectors.toList());

        if (responseList.size() > 0) {
            return ResponseEntity.ok(responseList);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PutMapping
    @ApiOperation(value = "Hinzufügen oder aktualisieren eines Autors.", response = AuthorDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Author erfolgreich aktualisiert.", response = AuthorDTO.class),
            @ApiResponse(code = 422, message = "Author konnte aufgrund fehlerhafter Eingabedaten nicht aktualisiert werden.")
    })/*
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Autor ID (nur bei Update notwendig)", required = false, dataType = "Long", dataTypeClass = Long.class, paramType = "query")
    })*/
    public ResponseEntity<AuthorDTO> addOrUpdateAuthor(@ApiParam(value = "Neuer oder aktualisierter Autor. Autor ID nur bei Update notwendig.", required = true) @RequestBody final AuthorUpdateDTO updatedAuthor) {
        try {
            final AuthorEntity authorEntity = modelMapper.map(updatedAuthor, AuthorEntity.class);
            return ResponseEntity.ok(modelMapper.map(authorService.addOrUpdateAuthor(authorEntity), AuthorDTO.class));
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    @DeleteMapping(value = "/{authorId}")
    @ApiOperation(value = "Löschen eines Autors mit einer bestimmten ID. Führt automatisch zur Löschung aller Bücher eines Autors.", response = Boolean.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Autor erfolgreich gelöscht.", response = Boolean.class),
            @ApiResponse(code = 404, message = "Keinen Autor mit dieser ID vorhanden.", response = Boolean.class)
    })
    public ResponseEntity<Boolean> deleteAuthor(@ApiParam(value = "Autor ID", required = true) @PathVariable("authorId") final long authorId) {
        if (authorService.deleteAuthor(authorId)) {
            return ResponseEntity.ok(Boolean.TRUE);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Helper methods

    @ApiIgnore
    private AuthorDTO streamConvertHelper(final AuthorEntity authorEntity) {
        return modelMapper.map(authorEntity, AuthorDTO.class);
    }
}
