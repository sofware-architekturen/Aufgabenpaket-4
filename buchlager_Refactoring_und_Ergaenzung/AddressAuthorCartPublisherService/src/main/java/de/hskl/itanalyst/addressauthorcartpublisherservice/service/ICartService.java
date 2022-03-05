package de.hskl.itanalyst.addressauthorcartpublisherservice.service;

import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.model.CartEntity;

import java.util.Optional;

public interface ICartService {
    Optional<CartEntity> addToCart(final String sessionId, final long bookId, final long amount);

    Optional<CartEntity> removeFromCart(final String sessionId, final long bookId, final long amount);

    Optional<CartEntity> removeItemFromCart(final String sessionId, final long bookId);

    CartEntity getCart(final String sessionId);

    Optional<CartEntity> changeCartItem(final String sessionId, final long bookId, final long amount);

    CartEntity checkoutCart(final String sessionId);
}
