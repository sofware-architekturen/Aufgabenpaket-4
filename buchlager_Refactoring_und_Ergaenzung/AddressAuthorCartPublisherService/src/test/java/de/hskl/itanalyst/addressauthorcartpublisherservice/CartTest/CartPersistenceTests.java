package de.hskl.itanalyst.addressauthorcartpublisherservice.CartTest;

import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.model.*;
import de.hskl.itanalyst.addressauthorcartpublisherservice.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class CartPersistenceTests {
    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    private AddressEntity addressEntity;
    private BookEntity bookEntity;
    private PublisherEntity publisherEntity;
    private AuthorEntity authorEntity;
    private CartEntity cartEntity;
    private CartItemEntity cartItemEntity;

    @Test
    public void contextLoads() throws Exception {
        assertThat(publisherRepository).isNotNull();
        assertThat(addressRepository).isNotNull();
        assertThat(bookRepository).isNotNull();
        assertThat(authorRepository).isNotNull();
        assertThat(cartRepository).isNotNull();
        assertThat(cartItemRepository).isNotNull();
    }

    @BeforeEach
    public void beforeEach() {
        addressEntity = new AddressEntity("mustercity");
        addressRepository.save(addressEntity);
        publisherEntity = new PublisherEntity(addressEntity, "musterpublisher");
        publisherEntity = publisherRepository.save(publisherEntity);
        authorEntity = new AuthorEntity("Max", "Mustermann");
        authorEntity = authorRepository.save(authorEntity);
        bookEntity = new BookEntity(publisherEntity, new HashSet<>(List.of(authorEntity)), "Mustertitel", 10);
        bookEntity = bookRepository.save(bookEntity);

        authorEntity = authorRepository.save(authorEntity);
        publisherEntity = publisherRepository.findById(publisherEntity.getId()).get();
        publisherEntity.addBook(bookEntity);
        publisherEntity = publisherRepository.save(publisherEntity);
        publisherEntity = publisherRepository.findById(publisherEntity.getId()).get();
        bookEntity = bookRepository.findById(bookEntity.getId()).get();

        cartEntity = new CartEntity();
        cartItemEntity = new CartItemEntity(1, bookEntity);
        cartEntity = cartRepository.save(cartEntity);
        cartItemEntity = cartItemRepository.save(cartItemEntity);
        cartEntity.addItem(cartItemEntity);
        cartEntity = cartRepository.save(cartEntity);
        cartItemEntity = cartItemRepository.save(cartItemEntity);
    }

    @Test
    public void whenDeleteCart_thenDeleteCartAndItems() {
        cartRepository.delete(cartEntity);

        // make sure cart and items are deleted
        Iterable<CartEntity> carts = cartRepository.findAll();
        assertThat(carts.iterator().hasNext()).isFalse();

        Iterable<CartItemEntity> cartItems = cartItemRepository.findAll();
        assertThat(cartItems.iterator().hasNext()).isFalse();

        Iterable<BookEntity> allBooks = bookRepository.findAll();
        // Make sure the book has not been deleted
        assertThat(allBooks.iterator().hasNext()).isTrue();

        Iterable<PublisherEntity> allPublishers = publisherRepository.findAll();
        // Publisher should still exist
        assertThat(allPublishers.iterator().hasNext()).isTrue();

        Iterable<AuthorEntity> allAuthors = authorRepository.findAll();
        // Author should still exist
        assertThat(allAuthors.iterator().hasNext()).isTrue();
    }

    @Test
    public void whenDeleteCartItem_thenDeleteCarItemOnly() {
        cartItemRepository.delete(cartItemEntity);

        // make sure only the items are deleted and the cart still exists
        Iterable<CartItemEntity> cartItems = cartItemRepository.findAll();
        assertThat(cartItems.iterator().hasNext()).isFalse();

        Iterable<CartEntity> carts = cartRepository.findAll();
        assertThat(carts.iterator().hasNext()).isTrue();
        assertThat(carts.iterator().next().getItems().size()).isEqualTo(0);


        Iterable<BookEntity> allBooks = bookRepository.findAll();
        // Make sure the book has not been deleted
        assertThat(allBooks.iterator().hasNext()).isTrue();

        Iterable<PublisherEntity> allPublishers = publisherRepository.findAll();
        // Publisher should still exist
        assertThat(allPublishers.iterator().hasNext()).isTrue();

        Iterable<AuthorEntity> allAuthors = authorRepository.findAll();
        // Author should still exist
        assertThat(allAuthors.iterator().hasNext()).isTrue();
    }

    @Test
    public void whenFindBySessionId_thenCart() {
        // given

        // when
        Optional<CartEntity> foundinRepo = cartRepository.findBySessionId(cartEntity.getSessionId());

        // then
        assertThat(foundinRepo).isPresent();
        foundinRepo.ifPresent(cartEntities -> {
            assertThat(cartEntities.getSessionId()).isEqualTo(cartEntity.getSessionId());
            assertThat(cartEntities.getItems().size()).isEqualTo(1);
        });
    }

    @Test
    public void whenFindByWrongSessionId_thenCart() {
        // given

        // when
        Optional<CartEntity> foundinRepo = cartRepository.findBySessionId(cartEntity.getSessionId() + "a");

        // then
        assertThat(foundinRepo).isEmpty();
    }

    @Test
    public void whenDeleteTimedOutCartSessions_thenReturnNoCarts() {
        // given
        cartRepository.deleteByValidUntilBefore(LocalDateTime.now().plus(1, ChronoUnit.HOURS));

        // when
        Optional<CartEntity> foundinRepo = cartRepository.findBySessionId(cartEntity.getSessionId());

        // then
        assertThat(foundinRepo).isEmpty();

        // make sure cart and items are deleted
        Iterable<CartEntity> carts = cartRepository.findAll();
        assertThat(carts.iterator().hasNext()).isFalse();

        Iterable<CartItemEntity> cartItems = cartItemRepository.findAll();
        assertThat(cartItems.iterator().hasNext()).isFalse();

        Iterable<BookEntity> allBooks = bookRepository.findAll();
        // Make sure the book has not been deleted
        assertThat(allBooks.iterator().hasNext()).isTrue();

        Iterable<PublisherEntity> allPublishers = publisherRepository.findAll();
        // Publisher should still exist
        assertThat(allPublishers.iterator().hasNext()).isTrue();

        Iterable<AuthorEntity> allAuthors = authorRepository.findAll();
        // Author should still exist
        assertThat(allAuthors.iterator().hasNext()).isTrue();
    }

    @Test
    public void whenDeleteTimedOutCartSessions_thenReturnCartsNotTimedOut() {
        // given
        cartRepository.deleteByValidUntilBefore(LocalDateTime.now().plus(1, ChronoUnit.SECONDS));

        // when
        Optional<CartEntity> foundinRepo = cartRepository.findBySessionId(cartEntity.getSessionId());

        // then
        assertThat(foundinRepo).isNotEmpty();

        // make sure cart and items are still available
        Iterable<CartEntity> carts = cartRepository.findAll();
        assertThat(carts.iterator().hasNext()).isTrue();
        assertThat(carts.iterator().next().getItems().size()).isEqualTo(1);

        Iterable<CartItemEntity> cartItems = cartItemRepository.findAll();
        assertThat(cartItems.iterator().hasNext()).isTrue();

        Iterable<BookEntity> allBooks = bookRepository.findAll();
        // Make sure the book has not been deleted
        assertThat(allBooks.iterator().hasNext()).isTrue();

        Iterable<PublisherEntity> allPublishers = publisherRepository.findAll();
        // Publisher should still exist
        assertThat(allPublishers.iterator().hasNext()).isTrue();

        Iterable<AuthorEntity> allAuthors = authorRepository.findAll();
        // Author should still exist
        assertThat(allAuthors.iterator().hasNext()).isTrue();
    }
}
