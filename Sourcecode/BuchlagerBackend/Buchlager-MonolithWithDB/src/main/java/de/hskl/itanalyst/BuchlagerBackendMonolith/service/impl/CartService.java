package de.hskl.itanalyst.BuchlagerBackendMonolith.service.impl;

import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.BookEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.CartEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.CartItemEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.repository.BookRepository;
import de.hskl.itanalyst.BuchlagerBackendMonolith.repository.CartItemRepository;
import de.hskl.itanalyst.BuchlagerBackendMonolith.repository.CartRepository;
import de.hskl.itanalyst.BuchlagerBackendMonolith.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class CartService implements ICartService {
    public static final int VALIDITY_TIME_HOURS = 3;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private BookRepository bookRepository;

    public Optional<CartEntity> addToCart(final String sessionId, final long bookId, final long amount) {
        if (bookId < 0) {
            return Optional.empty();
        }
        final Optional<BookEntity> bookEntity = bookRepository.findById(bookId);
        if (bookEntity.isEmpty()) {
            return Optional.empty();
        }
        final Optional<CartEntity> cartEntity = cartRepository.findBySessionId(sessionId);
        if (cartEntity.isPresent()) {
            cartEntity.get().setValidUntil(LocalDateTime.now().plus(VALIDITY_TIME_HOURS, ChronoUnit.HOURS)); // extend time due to this access
            Optional<CartItemEntity> cartItemEntityMatch = cartEntity.get().getItems().stream().filter(cartItemEntity -> cartItemEntity.getItem().equals(bookEntity.get())).findFirst();
            if (cartItemEntityMatch.isPresent()) {
                if ((cartItemEntityMatch.get().getAmount() + amount) < 1) {
                    return removeFromCart(sessionId, bookId, -amount);
                } else {
                    cartItemEntityMatch.get().setAmount(cartItemEntityMatch.get().getAmount() + amount);
                }
            } else {
                final CartItemEntity newCartItemEntity = new CartItemEntity(amount, bookEntity.get());
                cartItemRepository.save(newCartItemEntity);
                cartEntity.get().getItems().add(newCartItemEntity);
            }
            return Optional.of(cartRepository.save(cartEntity.get()));
        } else {
            final CartItemEntity newCartItemEntity = new CartItemEntity(amount, bookEntity.get());
            final CartEntity newCartEntity = new CartEntity(sessionId, LocalDateTime.now().plus(VALIDITY_TIME_HOURS, ChronoUnit.HOURS), Set.of(newCartItemEntity));
            cartItemRepository.save(newCartItemEntity);
            return Optional.of(cartRepository.save(newCartEntity));
        }
    }

    public Optional<CartEntity> removeFromCart(final String sessionId, final long bookId, final long amount) {
        if (bookId < 0) {
            return Optional.empty();
        }
        final Optional<BookEntity> bookEntity = bookRepository.findById(bookId);
        if (bookEntity.isEmpty()) {
            return Optional.empty();
        }
        final Optional<CartEntity> cartEntity = cartRepository.findBySessionId(sessionId);
        if (cartEntity.isPresent()) {
            cartEntity.get().setValidUntil(LocalDateTime.now().plus(VALIDITY_TIME_HOURS, ChronoUnit.HOURS)); // extend time due to this access
            Optional<CartItemEntity> cartItemEntityMatch = cartEntity.get().getItems().stream().filter(cartItemEntity -> cartItemEntity.getItem().equals(bookEntity.get())).findFirst();
            if (cartItemEntityMatch.isPresent()) {
                final long newAmount = cartItemEntityMatch.get().getAmount() - amount;
                if (newAmount < 1) {
                    cartEntity.get().getItems().remove(cartItemEntityMatch.get());
                    cartItemRepository.delete(cartItemEntityMatch.get());
                } else {
                    cartItemEntityMatch.get().setAmount(newAmount);
                    cartItemRepository.save(cartItemEntityMatch.get());
                }
                return Optional.of(cartRepository.save(cartEntity.get()));
            } else {
                return Optional.of(cartEntity.get());
            }
        } else {
            final CartEntity newCartEntity = new CartEntity(sessionId, LocalDateTime.now().plus(VALIDITY_TIME_HOURS, ChronoUnit.HOURS), new HashSet<>(1));
            return Optional.of(cartRepository.save(newCartEntity));
        }
    }

    public Optional<CartEntity> removeItemFromCart(final String sessionId, final long bookId) {
        if (bookId < 0) {
            return Optional.empty();
        }
        final Optional<BookEntity> bookEntity = bookRepository.findById(bookId);
        if (bookEntity.isEmpty()) {
            return Optional.empty();
        }
        final Optional<CartEntity> cartEntity = cartRepository.findBySessionId(sessionId);
        if (cartEntity.isPresent()) {
            cartEntity.get().setValidUntil(LocalDateTime.now().plus(VALIDITY_TIME_HOURS, ChronoUnit.HOURS)); // extend time due to this access
            Optional<CartItemEntity> cartItemEntityMatch = cartEntity.get().getItems().stream().filter(cartItemEntity -> cartItemEntity.getItem().equals(bookEntity.get())).findFirst();
            if (cartItemEntityMatch.isPresent()) {
                cartEntity.get().getItems().remove(cartItemEntityMatch.get());
                cartItemRepository.delete(cartItemEntityMatch.get());
                return Optional.of(cartRepository.save(cartEntity.get()));
            } else {
                return Optional.of(cartEntity.get());
            }
        } else {
            final CartEntity newCartEntity = new CartEntity(sessionId, LocalDateTime.now().plus(VALIDITY_TIME_HOURS, ChronoUnit.HOURS), new HashSet<>(1));
            return Optional.of(cartRepository.save(newCartEntity));
        }
    }

    public CartEntity getCart(final String sessionId) {
        final Optional<CartEntity> cartEntity = cartRepository.findBySessionId(sessionId);
        if (cartEntity.isPresent()) {
            cartEntity.get().setValidUntil(LocalDateTime.now().plus(VALIDITY_TIME_HOURS, ChronoUnit.HOURS)); // extend time due to this access
            return cartRepository.save(cartEntity.get());
        } else {
            final CartEntity newCartEntity = new CartEntity(sessionId, LocalDateTime.now().plus(VALIDITY_TIME_HOURS, ChronoUnit.HOURS), new HashSet<>(1));
            return cartRepository.save(newCartEntity);
        }
    }

    public Optional<CartEntity> changeCartItem(final String sessionId, final long bookId, final long amount) {
        if (bookId < 0) {
            return Optional.empty();
        }
        final Optional<BookEntity> bookEntity = bookRepository.findById(bookId);
        if (bookEntity.isEmpty()) {
            return Optional.empty();
        }
        final Optional<CartEntity> cartEntity = cartRepository.findBySessionId(sessionId);
        if (cartEntity.isPresent()) {
            cartEntity.get().setValidUntil(LocalDateTime.now().plus(VALIDITY_TIME_HOURS, ChronoUnit.HOURS)); // extend time due to this access
            Optional<CartItemEntity> cartItemEntityMatch = cartEntity.get().getItems().stream().filter(cartItemEntity -> cartItemEntity.getItem().equals(bookEntity.get())).findFirst();
            if (cartItemEntityMatch.isPresent()) {
                if (amount < 1) {
                    return removeFromCart(sessionId, bookId, -amount);
                } else {
                    cartItemEntityMatch.get().setAmount(amount);
                }
            } else {
                final CartItemEntity newCartItemEntity = new CartItemEntity(amount, bookEntity.get());
                cartItemRepository.save(newCartItemEntity);
                cartEntity.get().getItems().add(newCartItemEntity);
            }
            return Optional.of(cartRepository.save(cartEntity.get()));
        } else {
            final CartItemEntity newCartItemEntity = new CartItemEntity(amount, bookEntity.get());
            final CartEntity newCartEntity = new CartEntity(sessionId, LocalDateTime.now().plus(VALIDITY_TIME_HOURS, ChronoUnit.HOURS), Set.of(newCartItemEntity));
            cartItemRepository.save(newCartItemEntity);
            return Optional.of(cartRepository.save(newCartEntity));
        }
    }

    public CartEntity checkoutCart(final String sessionId) {
        final Optional<CartEntity> cartEntity = cartRepository.findBySessionId(sessionId);
        if (cartEntity.isPresent()) {
            cartEntity.get().setValidUntil(LocalDateTime.now().plus(VALIDITY_TIME_HOURS, ChronoUnit.HOURS)); // extend time due to this access
            cartEntity.get().getItems().forEach(cartItemEntity -> {
                long numberInStock = cartItemEntity.getItem().getAmount();
                long numberInCart = cartItemEntity.getAmount();
                if (numberInStock - numberInCart < 2) {
                    // Order new books from the central administration
                    // Simulate successful new order
                    cartItemEntity.getItem().setAmount(10);
                } else { // no need to order new books, just reduce the amount
                    cartItemEntity.getItem().setAmount(numberInStock - numberInCart);
                    bookRepository.save(cartItemEntity.getItem());
                }
            });
            cartEntity.get().getItems().clear();
            return cartRepository.save(cartEntity.get());
        } else {
            final CartEntity newCartEntity = new CartEntity(sessionId, LocalDateTime.now().plus(VALIDITY_TIME_HOURS, ChronoUnit.HOURS), new HashSet<>(1));
            return cartRepository.save(newCartEntity);
        }
    }
}
