package de.hskl.itanalyst.BuchlagerBackendMonolith.controller.api;

import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Cart.CartDTO;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.CartEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.service.ICartService;
import io.swagger.annotations.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/cart")
@Api(value = "Cart API - Verwalten des Warenkorbs")
public class CartAPI {
    @Autowired
    private ICartService cartService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping(value = "/{bookId}/{amount}")
    @ApiOperation(value = "Hinzufügen eines Buches mit einer bestimmten ID zum Warenkorb.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Buch erfolgreich hinzugefügt.", response = CartDTO.class),
            @ApiResponse(code = 404, message = "Kein Buch mit dieser ID vorhanden.")
    })
    public ResponseEntity<CartDTO> addToCart(@ApiIgnore final HttpSession session, @ApiParam(value = "Book ID", required = true) @PathVariable("bookId") final long bookId, @ApiParam(value = "Anzahl", required = true) @PathVariable("amount") final long amount) {
        final Optional<CartEntity> response = cartService.addToCart(session.getId(), bookId, amount);
        return response.map(cartEntity -> ResponseEntity.ok(modelMapper.map(cartEntity, CartDTO.class))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping(value = "/{bookId}/{amount}")
    @ApiOperation(value = "Löschen eines Buches mit einer bestimmten ID aus dem Warenkorb.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Buch erfolgreich gelöscht.", response = CartDTO.class),
            @ApiResponse(code = 404, message = "Kein Buch mit dieser ID vorhanden.")
    })
    public ResponseEntity<CartDTO> removeFromCart(@ApiIgnore final HttpSession session, @ApiParam(value = "Book ID", required = true) @PathVariable("bookId") final long bookId, @ApiParam(value = "Anzahl", required = true) @PathVariable("amount") final long amount) {
        final Optional<CartEntity> response = cartService.removeFromCart(session.getId(), bookId, amount);
        return response.map(cartEntity -> ResponseEntity.ok(modelMapper.map(cartEntity, CartDTO.class))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping(value = "/{bookId}")
    @ApiOperation(value = "Löschen eines Buches mit einer bestimmten ID aus dem Warenkorb.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Buch erfolgreich gelöscht.", response = CartDTO.class),
            @ApiResponse(code = 404, message = "Kein Buch mit dieser ID vorhanden.")
    })
    public ResponseEntity<CartDTO> removeItemFromCart(@ApiIgnore final HttpSession session, @ApiParam(value = "Book ID", required = true) @PathVariable("bookId") final long bookId) {
        final Optional<CartEntity> response = cartService.removeItemFromCart(session.getId(), bookId);
        return response.map(cartEntity -> ResponseEntity.ok(modelMapper.map(cartEntity, CartDTO.class))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @ApiOperation(value = "Aktuellen Warenkorbinhalt abrufen.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Warenbuch erfolgreich gesendet.", response = CartDTO.class),
    })
    public ResponseEntity<CartDTO> getCart(@ApiIgnore final HttpSession session) {
        final CartEntity cartEntity = cartService.getCart(session.getId());
        return ResponseEntity.ok(modelMapper.map(cartEntity, CartDTO.class));
    }

    @PutMapping(value = "/{bookId}/{amount}")
    @ApiOperation(value = "Ändern der Anzahl eines Buches mit einer bestimmten ID im Warenkorb.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Anzahl erfolgreich geändert.", response = CartDTO.class),
            @ApiResponse(code = 404, message = "Kein Buch mit dieser ID vorhanden.")
    })
    public ResponseEntity<CartDTO> changeCartItem(@ApiIgnore final HttpSession session, @ApiParam(value = "Book ID", required = true) @PathVariable("bookId") final long bookId, @ApiParam(value = "Anzahl", required = true) @PathVariable("amount") final long amount) {
        final Optional<CartEntity> response = cartService.changeCartItem(session.getId(), bookId, amount);
        return response.map(cartEntity -> ResponseEntity.ok(modelMapper.map(cartEntity, CartDTO.class))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/checkout")
    @ApiOperation(value = "Einkauf abschließen -> Warenkorb löschen.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Einkauf erfolgreich abgeschlossen.", response = CartDTO.class)
    })
    public ResponseEntity<CartDTO> checkoutCart(@ApiIgnore final HttpSession session) {
        final CartEntity cartEntity = cartService.checkoutCart(session.getId());
        return ResponseEntity.ok(modelMapper.map(cartEntity, CartDTO.class));
    }
}
