package de.hskl.itanalyst.buchservice.controller.api;

import de.hskl.itanalyst.buchservice.domain.dto.BookDTO;
import de.hskl.itanalyst.buchservice.domain.dto.BookUpdateDTO;
import de.hskl.itanalyst.buchservice.domain.model.BookEntity;
import de.hskl.itanalyst.buchservice.service.IBookService;
import io.swagger.annotations.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping(path = "/api/books")
@Api(value = "Book API - Verwalten aller Bücher")
public class BookAPI {
    @Autowired
    private IBookService bookService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping(value = "/{bookId}")
    @ApiOperation(value = "Anzeigen eines Buches mit einer bestimmten ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Buch erfolgreich geliefert.", response = BookDTO.class),
            @ApiResponse(code = 404, message = "Kein Buch mit dieser ID vorhanden.")
    })
    public ResponseEntity<BookDTO> bookById(@ApiParam(value = "Book ID", required = true) @PathVariable("bookId") final long bookId) {
        final Optional<BookEntity> responseEntity = bookService.getBookById(bookId);
        return responseEntity.map(bookEntity -> ResponseEntity.ok(modelMapper.map(bookEntity, BookDTO.class))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @ApiOperation(value = "Alle verfügbaren Bücher anzeigen.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Liste erfolgreich geliefert.", responseContainer = "List", response = BookDTO.class),
            @ApiResponse(code = 204, message = "Keine Bücher vorhanden.")
    })
    public ResponseEntity<List<BookDTO>> allBooks() {
        final Stream<BookEntity> bookEntityStreamHelper = bookService.getAllBooksAsStream();
        final List<BookDTO> responseList = bookEntityStreamHelper.map(this::streamConvertHelper).collect(Collectors.toList());

        if (responseList.size() > 0) {
            return ResponseEntity.ok(responseList);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PutMapping
    @ApiOperation(value = "Hinzufügen oder aktualisieren eines Buches.", response = BookDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Buch erfolgreich aktualisiert.", response = BookDTO.class),
            @ApiResponse(code = 422, message = "Buch konnte aufgrund fehlerhafter Eingabedaten nicht aktualisiert werden.")
    })
    /*
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "Buch ID (nur bei Update notwendig)", required = false, dataType = "Long", dataTypeClass = Long.class, paramType = "query")
    })*/
    public ResponseEntity<BookDTO> addOrUpdateBook(@ApiParam(value = "Neues oder aktualisiertes Buch. Buch ID nur bei Update notwendig.", required = true) @RequestBody final BookUpdateDTO updatedBook) {
        try {
            final BookEntity bookEntity = modelMapper.map(updatedBook, BookEntity.class);
            return ResponseEntity.ok(modelMapper.map(bookService.addOrUpdateBook(bookEntity), BookDTO.class));
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    @DeleteMapping(value = "/{bookId}")
    @ApiOperation(value = "Löschen eines Buches mit einer bestimmten ID.", response = Boolean.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Buch erfolgreich gelöscht.", response = Boolean.class),
            @ApiResponse(code = 404, message = "Kein Buch mit dieser ID vorhanden.", response = Boolean.class)
    })
    public ResponseEntity<Boolean> deleteBook(@ApiParam(value = "Buch ID", required = true) @PathVariable("bookId") final long bookId) {
        if (bookService.deleteBook(bookId)) {
            return ResponseEntity.ok(Boolean.TRUE);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/bookcover/{bookId}", produces = MediaType.IMAGE_PNG_VALUE)
    @ApiOperation(value = "Buchcover eines Buches mit einer bestimmten ID anzeigen.", response = byte[].class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Buchcover erfolgreich gelöscht.", response = byte[].class),
            @ApiResponse(code = 404, message = "Kein Buch mit dieser ID vorhanden.")
    })
    public ResponseEntity<byte[]> getBookCover(@ApiParam(value = "Buch ID", required = true) @PathVariable("bookId") final long bookId) {
        final Optional<BufferedImage> bookCover = bookService.getBookCover(bookId);

        try {
            if (bookCover.isPresent()) {
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bookCover.get(), "png", baos);
                return ResponseEntity.ok(baos.toByteArray());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Helper methods

    @ApiIgnore
    private BookDTO streamConvertHelper(final BookEntity bookEntity) {
        return modelMapper.map(bookEntity, BookDTO.class);
    }
}
