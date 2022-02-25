package de.hskl.itanalyst.BuchlagerBackendMonolith.CartTest;

import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.*;
import de.hskl.itanalyst.BuchlagerBackendMonolith.repository.BookRepository;
import de.hskl.itanalyst.BuchlagerBackendMonolith.repository.CartItemRepository;
import de.hskl.itanalyst.BuchlagerBackendMonolith.repository.CartRepository;
import de.hskl.itanalyst.BuchlagerBackendMonolith.service.ICartService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CartServiceTests {
    @Autowired
    private ICartService cartService;

    @MockBean
    private CartRepository cartRepository;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private CartItemRepository cartItemRepository;

    private final AuthorEntity authorEntity = new AuthorEntity("Max", "Mustermann", new HashSet<>(1));
    private final AddressEntity addressEntity = new AddressEntity("musterhausen");
    private final PublisherEntity publisherEntity = new PublisherEntity(addressEntity, new HashSet<>(1), "Musterverlag");
    private final BookEntity bookEntity = new BookEntity(publisherEntity, Set.of(authorEntity), "Mustertitel", 10);
    private final BookEntity bookEntity2 = new BookEntity(publisherEntity, Set.of(authorEntity), "Mustertitel2", 10);
    private final CartItemEntity cartItemEntity = new CartItemEntity(1, bookEntity);
    private final CartEntity cartEntity = new CartEntity("0", LocalDateTime.now().plus(1, ChronoUnit.MINUTES), Set.of(cartItemEntity));

    @BeforeEach
    public void setUp() {
        Mockito.when(cartRepository.findBySessionId("0")).thenReturn(Optional.of(cartEntity));
        Mockito.when(cartRepository.findBySessionId("1")).thenReturn(Optional.empty());
        Mockito.when(bookRepository.findById(0L)).thenReturn(Optional.of(bookEntity));
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        Spliterator<CartEntity> spliterator = Arrays.spliterator(new CartEntity[]{cartEntity});
        Iterable<CartEntity> authorEntityIterable = new Iterable<CartEntity>() {
            @Override
            public Spliterator<CartEntity> spliterator() {
                return spliterator;
            }

            @Override
            public Iterator<CartEntity> iterator() {
                return new Iterator<CartEntity>() {
                    private boolean hasNext = true;

                    @Override
                    public boolean hasNext() {
                        return hasNext;
                    }

                    @Override
                    public CartEntity next() {
                        try {
                            return cartEntity;
                        } finally {
                            hasNext = false;
                        }
                    }
                };
            }
        };
    }

    @AfterEach
    public void afterEach() {
        // reset mock
        Mockito.reset(cartRepository);
        Mockito.reset(bookRepository);
        Mockito.reset(cartItemRepository);
    }

    @Test
    public void whenValidId_thenCartShouldBeFound() {
        Mockito.when(cartRepository.findBySessionId("0")).thenReturn(Optional.of(cartEntity));
        Mockito.when(cartRepository.save(Mockito.any())).thenReturn(cartEntity);
        CartEntity foundService = cartService.getCart("0");

        assertThat(foundService).isNotNull();
        assertThat(foundService.getValidUntil()).isEqualTo(cartEntity.getValidUntil());
    }

    @Test
    public void whenInvalidId_thenCartShouldBeCreated() {
        Mockito.when(cartRepository.findBySessionId("0")).thenReturn(Optional.empty());
        Mockito.when(cartRepository.save(Mockito.any())).thenReturn(cartEntity);
        CartEntity foundService = cartService.getCart("0");

        assertThat(foundService).isNotNull();
        assertThat(foundService.getValidUntil()).isEqualTo(cartEntity.getValidUntil());
        Mockito.verify(cartRepository, VerificationModeFactory.atLeast(1)).save(foundService);
    }

    @Test
    public void whenAddToNotExistingCart_thenCartShouldBeCreatedWithItemAndReturned() {
        Mockito.when(cartRepository.findBySessionId("0")).thenReturn(Optional.empty());
        Mockito.when(cartRepository.save(Mockito.any())).thenReturn(cartEntity);
        Mockito.when(cartItemRepository.save(Mockito.any())).thenReturn(null);
        Optional<CartEntity> foundService = cartService.addToCart("0", 0L, 1L);

        assertThat(foundService).isNotEmpty();
        assertThat(foundService.get().getItems().size()).isEqualTo(1);
        assertThat(foundService.get().getItems().iterator().next().getItem()).isEqualTo(bookEntity);
        Mockito.verify(cartRepository, VerificationModeFactory.atLeast(1)).save(cartEntity);
    }

    @Test
    public void whenAddToExistingCart_thenItemShouldBeAddedAndCartEntityReturned() {
        Mockito.when(cartRepository.findBySessionId("0")).thenReturn(Optional.of(cartEntity));
        Mockito.when(cartRepository.save(Mockito.any())).thenReturn(cartEntity);
        Mockito.when(cartItemRepository.save(Mockito.any())).thenReturn(null);
        Optional<CartEntity> foundService = cartService.addToCart("0", 0L, 1L);

        assertThat(foundService).isNotEmpty();
        assertThat(foundService.get().getItems().size()).isEqualTo(1);
        assertThat(foundService.get().getItems().iterator().next().getItem()).isEqualTo(bookEntity);
        Mockito.verify(cartRepository, VerificationModeFactory.atLeast(1)).save(cartEntity);
    }

    @Test
    public void whenAddInvalidDataToExistingCart_thenOptionalEmptyShouldBeReturned() {
        Mockito.when(cartRepository.findBySessionId("0")).thenReturn(Optional.of(cartEntity));
        Mockito.when(cartRepository.save(Mockito.any())).thenReturn(cartEntity);
        Mockito.when(cartItemRepository.save(Mockito.any())).thenReturn(null);

        Optional<CartEntity> foundService = cartService.addToCart("0", -1L, 1L);
        assertThat(foundService).isEmpty();

        foundService = cartService.addToCart("0", 1L, 1L);
        assertThat(foundService).isEmpty();
    }

    @Test
    public void whenAddExistingItemsToExistingCart_thenCartShouldBeReturned() {
        Mockito.when(cartRepository.findBySessionId("0")).thenReturn(Optional.of(cartEntity));
        Mockito.when(cartRepository.save(Mockito.any())).thenReturn(cartEntity);
        Mockito.when(cartItemRepository.save(Mockito.any())).thenReturn(null);
        LocalDateTime currentInCart = cartEntity.getValidUntil();
        LocalDateTime nowPlus = LocalDateTime.now().plus(3, ChronoUnit.HOURS);

        Optional<CartEntity> foundService = cartService.addToCart("0", 0L, 1L);
        assertThat(foundService).isNotEmpty();
        assertThat(foundService.get().getItems().size()).isEqualTo(1);
        assertThat(foundService.get().getItems().iterator().next().getAmount()).isEqualTo(2);
        assertThat(currentInCart).isNotEqualTo(foundService.get().getValidUntil());
        assertThat(foundService.get().getValidUntil().isAfter(nowPlus)).isTrue();

        foundService = cartService.addToCart("0", 0L, 1L);
        assertThat(foundService).isNotEmpty();
        assertThat(foundService.get().getItems().size()).isEqualTo(1);
        assertThat(foundService.get().getItems().iterator().next().getAmount()).isEqualTo(3);

        foundService = cartService.addToCart("0", 0L, -4L);
        assertThat(foundService).isNotEmpty();
        assertThat(foundService.get().getItems().size()).isEqualTo(0);
        assertThat(currentInCart).isNotEqualTo(foundService.get().getValidUntil());
        assertThat(foundService.get().getValidUntil().isAfter(nowPlus)).isTrue();
    }

    @Test
    public void whenRemoveInvalidDataToExistingCart_thenOptionalEmptyShouldBeReturned() {
        Mockito.when(cartRepository.findBySessionId("0")).thenReturn(Optional.of(cartEntity));
        Mockito.when(cartRepository.save(Mockito.any())).thenReturn(cartEntity);
        Mockito.when(cartItemRepository.save(Mockito.any())).thenReturn(null);

        Optional<CartEntity> foundService = cartService.removeFromCart("0", -1L, 1L);
        assertThat(foundService).isEmpty();

        foundService = cartService.removeFromCart("0", 1L, 1L);
        assertThat(foundService).isEmpty();
    }

    @Test
    public void whenRemoveWithInvalidSessionId_thenNewCartShouldBeReturned() {
        Mockito.when(cartRepository.findBySessionId("0")).thenReturn(Optional.of(cartEntity));
        Mockito.when(cartRepository.save(Mockito.any())).thenReturn(cartEntity);
        Mockito.when(cartItemRepository.save(Mockito.any())).thenReturn(null);

        Optional<CartEntity> foundService = cartService.removeFromCart("1", 0L, 1L);
        assertThat(foundService).isNotEmpty();
        assertThat(foundService.get()).isEqualTo(cartEntity);
    }

    @Test
    public void whenRemoveItemWithInvalidSessionId_thenNewCartShouldBeReturned() {
        Mockito.when(cartRepository.findBySessionId("0")).thenReturn(Optional.of(cartEntity));
        Mockito.when(cartRepository.save(Mockito.any())).thenReturn(cartEntity);
        Mockito.when(cartItemRepository.save(Mockito.any())).thenReturn(null);

        Optional<CartEntity> foundService = cartService.removeItemFromCart("1", 0L);
        assertThat(foundService).isNotEmpty();
        assertThat(foundService.get()).isEqualTo(cartEntity);
    }

    @Test
    public void whenRemoveExistingItemsFromExistingCart_thenCartShouldBeReturned() {
        Mockito.when(cartRepository.findBySessionId("0")).thenReturn(Optional.of(cartEntity));
        Mockito.when(cartRepository.save(Mockito.any())).thenReturn(cartEntity);
        Mockito.when(cartItemRepository.save(Mockito.any())).thenReturn(null);
        LocalDateTime currentInCart = cartEntity.getValidUntil();
        LocalDateTime nowPlus = LocalDateTime.now().plus(3, ChronoUnit.HOURS);

        Optional<CartEntity> foundService = cartService.addToCart("0", 0L, 1L);
        assertThat(foundService).isNotEmpty();
        assertThat(foundService.get().getItems().size()).isEqualTo(1);
        assertThat(foundService.get().getItems().iterator().next().getAmount()).isEqualTo(2);
        assertThat(currentInCart).isNotEqualTo(foundService.get().getValidUntil());
        assertThat(foundService.get().getValidUntil().isAfter(nowPlus)).isTrue();

        foundService = cartService.removeFromCart("0", 0L, 1L);
        assertThat(foundService).isNotEmpty();
        assertThat(foundService.get().getItems().size()).isEqualTo(1);
        assertThat(foundService.get().getItems().iterator().next().getAmount()).isEqualTo(1);

        foundService = cartService.removeFromCart("0", 0L, 1L);
        assertThat(foundService).isNotEmpty();
        assertThat(foundService.get().getItems().size()).isEqualTo(0);
        assertThat(currentInCart).isNotEqualTo(foundService.get().getValidUntil());
        assertThat(foundService.get().getValidUntil().isAfter(nowPlus)).isTrue();
    }

    @Test
    public void whenRemoveItemInvalidDataToExistingCart_thenOptionalEmptyShouldBeReturned() {
        Mockito.when(cartRepository.findBySessionId("0")).thenReturn(Optional.of(cartEntity));
        Mockito.when(cartRepository.save(Mockito.any())).thenReturn(cartEntity);
        Mockito.when(cartItemRepository.save(Mockito.any())).thenReturn(null);

        Optional<CartEntity> foundService = cartService.removeItemFromCart("0", -1L);
        assertThat(foundService).isEmpty();

        foundService = cartService.removeItemFromCart("0", 1L);
        assertThat(foundService).isEmpty();
    }

    @Test
    public void whenRemoveItemExistingItemsFromExistingCart_thenCartShouldBeReturned() {
        Mockito.when(cartRepository.findBySessionId("0")).thenReturn(Optional.of(cartEntity));
        Mockito.when(cartRepository.save(Mockito.any())).thenReturn(cartEntity);
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(bookEntity2));
        Mockito.when(cartItemRepository.save(Mockito.any())).thenReturn(null);
        Mockito.doNothing().when(cartItemRepository).delete(Mockito.any());
        LocalDateTime currentInCart = cartEntity.getValidUntil();
        LocalDateTime nowPlus = LocalDateTime.now().plus(3, ChronoUnit.HOURS);

        cartEntity.getItems().clear();

        Optional<CartEntity> foundService = cartService.addToCart("0", 0L, 2L);
        assertThat(foundService).isNotEmpty();
        assertThat(foundService.get().getItems().size()).isEqualTo(1);
        assertThat(foundService.get().getItems().iterator().next().getAmount()).isEqualTo(2);
        assertThat(currentInCart).isNotEqualTo(foundService.get().getValidUntil());
        assertThat(foundService.get().getValidUntil().isAfter(nowPlus)).isTrue();

        foundService = cartService.removeItemFromCart("0", 1L);
        assertThat(foundService).isNotEmpty();
        assertThat(foundService.get().getItems().size()).isEqualTo(1);
        assertThat(foundService.get().getItems().iterator().next().getAmount()).isEqualTo(2);
        assertThat(currentInCart).isNotEqualTo(foundService.get().getValidUntil());
        assertThat(foundService.get().getValidUntil().isAfter(nowPlus)).isTrue();

        foundService = cartService.removeItemFromCart("0", 0L);
        assertThat(foundService).isNotEmpty();
        assertThat(foundService.get().getItems().size()).isEqualTo(0);
        assertThat(currentInCart).isNotEqualTo(foundService.get().getValidUntil());
        assertThat(foundService.get().getValidUntil().isAfter(nowPlus)).isTrue();
    }

    @Test
    public void whenChangeItemInvalidDataToExistingCart_thenOptionalEmptyShouldBeReturned() {
        Mockito.when(cartRepository.findBySessionId("0")).thenReturn(Optional.of(cartEntity));
        Mockito.when(cartRepository.save(Mockito.any())).thenReturn(cartEntity);
        Mockito.when(cartItemRepository.save(Mockito.any())).thenReturn(null);

        Optional<CartEntity> foundService = cartService.changeCartItem("0", -1L, 10);
        assertThat(foundService).isEmpty();

        foundService = cartService.changeCartItem("0", 1L, 10);
        assertThat(foundService).isEmpty();
    }

    @Test
    public void whenChangeExistingItemsFromExistingCart_thenCartShouldBeReturned() {
        Mockito.when(cartRepository.findBySessionId("0")).thenReturn(Optional.of(cartEntity));
        Mockito.when(cartRepository.save(Mockito.any())).thenReturn(cartEntity);
        Mockito.when(cartItemRepository.save(Mockito.any())).thenReturn(null);
        LocalDateTime currentInCart = cartEntity.getValidUntil();
        LocalDateTime nowPlus = LocalDateTime.now().plus(3, ChronoUnit.HOURS);

        assertThat(cartEntity.getItems().size()).isEqualTo(1);
        assertThat(cartEntity.getItems().iterator().next().getAmount()).isEqualTo(1);

        Optional<CartEntity> foundService = cartService.changeCartItem("0", 0L, 10L);
        assertThat(foundService).isNotEmpty();
        assertThat(foundService.get().getItems().size()).isEqualTo(1);
        assertThat(foundService.get().getItems().iterator().next().getAmount()).isEqualTo(10);
        assertThat(currentInCart).isNotEqualTo(foundService.get().getValidUntil());
        assertThat(foundService.get().getValidUntil().isAfter(nowPlus)).isTrue();

        foundService = cartService.changeCartItem("0", 0L, -10L);
        assertThat(foundService).isNotEmpty();
        assertThat(foundService.get().getItems().size()).isEqualTo(0);

        foundService = cartService.changeCartItem("0", 0L, 10L);
        assertThat(foundService).isNotEmpty();
        assertThat(foundService.get().getItems().size()).isEqualTo(1);
        assertThat(foundService.get().getItems().iterator().next().getAmount()).isEqualTo(10);
        assertThat(currentInCart).isNotEqualTo(foundService.get().getValidUntil());
        assertThat(foundService.get().getValidUntil().isAfter(nowPlus)).isTrue();

        foundService = cartService.changeCartItem("1", 0L, 10L);
        assertThat(foundService).isNotEmpty();
        assertThat(foundService.get().getItems().size()).isEqualTo(1);
        assertThat(foundService.get().getItems().iterator().next().getAmount()).isEqualTo(10);
        assertThat(currentInCart).isNotEqualTo(foundService.get().getValidUntil());
        assertThat(foundService.get().getValidUntil().isAfter(nowPlus)).isTrue();
    }

    @Test
    public void whenCheckoutFromExistingCart_thenOptionalShouldBeReturned() {
        Mockito.when(cartRepository.findBySessionId("0")).thenReturn(Optional.of(cartEntity));
        Mockito.when(cartRepository.save(Mockito.any())).thenReturn(cartEntity);
        Mockito.when(cartItemRepository.save(Mockito.any())).thenReturn(null);

        LocalDateTime currentInCart = cartEntity.getValidUntil();
        LocalDateTime nowPlus = LocalDateTime.now().plus(3, ChronoUnit.HOURS);

        CartEntity foundService = cartService.checkoutCart("1");
        assertThat(foundService).isNotNull();
        assertThat(foundService.getItems().size()).isEqualTo(cartEntity.getItems().size());

        foundService = cartService.checkoutCart("0");
        assertThat(foundService).isNotNull();
        assertThat(foundService.getItems().size()).isEqualTo(0);

        assertThat(currentInCart).isNotEqualTo(foundService.getValidUntil());
        assertThat(foundService.getValidUntil().isAfter(nowPlus)).isTrue();
    }
}
