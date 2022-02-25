package de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Cart;

import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Book.BookLightDTO;

public class CartItemDTO {
    public long amount;
    public BookLightDTO book;
}
