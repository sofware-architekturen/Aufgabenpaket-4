package de.hskl.itanalyst.addressauthorcartpublisherservice.domain.dto.Cart;

import java.time.LocalDateTime;
import java.util.Set;

public class CartDTO {
    public Set<CartItemDTO> items;
    public LocalDateTime validUntil;
}
