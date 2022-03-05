package de.hskl.itanalyst.addressauthorcartpublisherservice.CartTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.dto.Cart.CartDTO;
import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.model.*;
import de.hskl.itanalyst.addressauthorcartpublisherservice.repository.CartRepository;
import de.hskl.itanalyst.addressauthorcartpublisherservice.service.ICartService;
import de.hskl.itanalyst.addressauthorcartpublisherservice.utils.ModelMapperUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CartAPITests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelMapperUtils modelMapperUtils;

    @MockBean
    private ICartService cartService;

    @Autowired
    private ModelMapper modelMapper;

    @MockBean
    private CartRepository cartRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    private final AuthorEntity authorEntity = new AuthorEntity("Max", "Mustermann", new HashSet<>(1));
    private final AddressEntity addressEntity = new AddressEntity("musterhausen");
    private final PublisherEntity publisherEntity = new PublisherEntity(addressEntity, new HashSet<>(1), "Musterverlag");
    private final BookEntity bookEntity = new BookEntity(publisherEntity, Set.of(authorEntity), "Mustertitel", 10);
    private final CartItemEntity cartItemEntity = new CartItemEntity(1, bookEntity);
    private final CartEntity cartEntity = new CartEntity("0", LocalDateTime.now().plus(1, ChronoUnit.MINUTES), Set.of(cartItemEntity));

    @BeforeEach
    public void setUp() {
        Mockito.when(cartRepository.findBySessionId(Mockito.anyString())).thenReturn(Optional.of(cartEntity));
        Mockito.when(cartService.addToCart(Mockito.anyString(), Mockito.eq(0L), Mockito.anyLong())).thenReturn(Optional.of(cartEntity));
        Mockito.when(cartService.addToCart(Mockito.anyString(), Mockito.eq(1L), Mockito.anyLong())).thenReturn(Optional.empty());
        Mockito.when(cartService.removeFromCart(Mockito.anyString(), Mockito.eq(0L), Mockito.anyLong())).thenReturn(Optional.of(cartEntity));
        Mockito.when(cartService.removeFromCart(Mockito.anyString(), Mockito.eq(1L), Mockito.anyLong())).thenReturn(Optional.empty());
        Mockito.when(cartService.removeItemFromCart(Mockito.anyString(), Mockito.eq(0L))).thenReturn(Optional.of(cartEntity));
        Mockito.when(cartService.removeItemFromCart(Mockito.anyString(), Mockito.eq(1L))).thenReturn(Optional.empty());
        Mockito.when(cartService.getCart(Mockito.anyString())).thenReturn(cartEntity);
        Mockito.when(cartService.checkoutCart(Mockito.anyString())).thenReturn(cartEntity);
        Mockito.when(cartService.changeCartItem(Mockito.anyString(), Mockito.eq(0L), Mockito.anyLong())).thenReturn(Optional.of(cartEntity));
        Mockito.when(cartService.changeCartItem(Mockito.anyString(), Mockito.eq(1L), Mockito.anyLong())).thenReturn(Optional.empty());
    }

    @PostConstruct
    private void postConstruct() {
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Test
    public void postAddToCart_ShouldReturnCart() throws Exception {
        this.mockMvc.perform(post("/api/cart/0/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(mapper.writeValueAsString(modelMapper.map(cartEntity, CartDTO.class))));
    }

    @Test
    public void postAddToCartWrongBook_ShouldReturnNotFound() throws Exception {
        this.mockMvc.perform(post("/api/cart/2/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content()
                        .string(""));
    }

    @Test
    public void deleteRemoveFromCart_ShouldReturnCart() throws Exception {
        this.mockMvc.perform(delete("/api/cart/0/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(mapper.writeValueAsString(modelMapper.map(cartEntity, CartDTO.class))));
    }

    @Test
    public void deleteRemoveFromCartWrongBook_ShouldReturnNotFound() throws Exception {
        this.mockMvc.perform(delete("/api/cart/2/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content()
                        .string(""));
    }

    @Test
    public void deleteRemoveItemFromCart_ShouldReturnCart() throws Exception {
        this.mockMvc.perform(delete("/api/cart/0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(mapper.writeValueAsString(modelMapper.map(cartEntity, CartDTO.class))));
    }

    @Test
    public void deleteRemoveItemFromCartWrongBook_ShouldReturnNotFound() throws Exception {
        this.mockMvc.perform(delete("/api/cart/2"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content()
                        .string(""));
    }

    @Test
    public void getCart_ShouldReturnCart() throws Exception {
        this.mockMvc.perform(get("/api/cart"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(mapper.writeValueAsString(modelMapper.map(cartEntity, CartDTO.class))));
    }

    @Test
    public void postCheckoutCart_ShouldReturnCart() throws Exception {
        this.mockMvc.perform(post("/api/cart/checkout"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(mapper.writeValueAsString(modelMapper.map(cartEntity, CartDTO.class))));
    }

    @Test
    public void putChangeCartItem_ShouldReturnCart() throws Exception {
        this.mockMvc.perform(put("/api/cart/0/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(mapper.writeValueAsString(modelMapper.map(cartEntity, CartDTO.class))));
    }

    @Test
    public void putChangeCartItemWrongBook_ShouldReturnNotFound() throws Exception {
        this.mockMvc.perform(put("/api/cart/2/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content()
                        .string(""));
    }
}
