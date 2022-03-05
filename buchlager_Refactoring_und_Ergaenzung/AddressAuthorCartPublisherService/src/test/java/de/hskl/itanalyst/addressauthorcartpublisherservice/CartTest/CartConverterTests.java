package de.hskl.itanalyst.addressauthorcartpublisherservice.CartTest;

import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.dto.Cart.CartDTO;
import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.dto.Cart.CartItemDTO;
import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CartConverterTests {
    @SpyBean
    private ModelMapper modelMapper;

    private final AuthorEntity authorEntity = new AuthorEntity("Max", "Mustermann", new HashSet<>(1));
    private final AddressEntity addressEntity = new AddressEntity("musterhausen");
    private final PublisherEntity publisherEntity = new PublisherEntity(addressEntity, new HashSet<>(1), "Musterverlag");
    private final BookEntity bookEntity = new BookEntity(publisherEntity, Set.of(authorEntity), "Mustertitel", 10);
    private final CartItemEntity cartItemEntity = new CartItemEntity(1, bookEntity);
    private final CartEntity cartEntity = new CartEntity("0", LocalDateTime.now().plus(1, ChronoUnit.MINUTES), Set.of(cartItemEntity));

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void contextLoads() throws Exception {
        assertThat(modelMapper).isNotNull();
    }

    @Test
    public void whenConvertCartEntityToDTO_thenShouldReturnCartDTO() {
        // given
        CartEntity cartEntity = new CartEntity();

        // when
        CartDTO cartDTO = modelMapper.map(cartEntity, CartDTO.class);

        // then
        assertThat(cartDTO).isNotNull();
        Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(cartEntity, CartDTO.class);
        assertThat(cartDTO.validUntil).isEqualTo(cartEntity.getValidUntil());
        assertThat(cartDTO.items.size()).isEqualTo(cartEntity.getItems().size());
        assertThat(cartDTO.items.size()).isEqualTo(0);
    }

    @Test
    public void whenConvertCartEntityWithItemToDTO_thenShouldReturnCartDTOWithItem() {
        // given
        CartEntity cartEntity = new CartEntity();
        CartItemEntity cartItemEntity = new CartItemEntity(1, bookEntity);
        cartEntity.addItem(cartItemEntity);

        // when
        CartDTO cartDTO = modelMapper.map(cartEntity, CartDTO.class);

        // then
        assertThat(cartDTO).isNotNull();
        Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(cartEntity, CartDTO.class);
        assertThat(cartDTO.validUntil).isEqualTo(cartEntity.getValidUntil());
        assertThat(cartDTO.items.size()).isEqualTo(cartEntity.getItems().size());
        assertThat(cartDTO.items.size()).isEqualTo(1);
        assertThat(cartDTO.items.iterator().next().book.title).isEqualTo(cartEntity.getItems().iterator().next().getItem().getTitle());
    }

    @Test
    public void whenConvertCartItemEntityToDTO_thenShouldReturnCartItemDTO() {
        // given
        CartEntity cartEntity = new CartEntity();
        CartItemEntity cartItemEntity = new CartItemEntity(1, bookEntity);
        cartEntity.addItem(cartItemEntity);

        // when
        CartItemDTO cartItemDTO = modelMapper.map(cartItemEntity, CartItemDTO.class);

        // then
        assertThat(cartItemDTO).isNotNull();
        Mockito.verify(modelMapper, VerificationModeFactory.times(1)).map(cartItemEntity, CartItemDTO.class);
        Mockito.verify(modelMapper, VerificationModeFactory.atLeastOnce()).addConverter(Mockito.any());
        assertThat(cartItemDTO.amount).isEqualTo(cartEntity.getItems().iterator().next().getAmount());
        assertThat(cartItemDTO.book.title).isEqualTo(cartEntity.getItems().iterator().next().getItem().getTitle());
    }
}
