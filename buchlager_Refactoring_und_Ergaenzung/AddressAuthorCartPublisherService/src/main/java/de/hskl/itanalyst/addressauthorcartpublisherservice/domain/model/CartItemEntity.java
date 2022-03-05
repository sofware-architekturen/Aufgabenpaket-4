package de.hskl.itanalyst.addressauthorcartpublisherservice.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "CartItemEntity")
public class CartItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private long id;

    private long amount;

    @ManyToOne
    @JoinColumn(name = "BOOK_ID")
    private de.hskl.itanalyst.addressauthorcartpublisherservice.domain.model.BookEntity item;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private CartEntity cart;

    @NaturalId
    private final UUID businessID;

    @PreRemove
    private void preRemove() {
        if (null != this.cart) {
            this.cart.removeItem(this);
        }
        this.cart = null;
        this.item = null;
    }

    public CartItemEntity() {
        this(-1, null);
    }

    public CartItemEntity(long amount, de.hskl.itanalyst.addressauthorcartpublisherservice.domain.model.BookEntity item) {
        this.amount = amount;
        setItem(item);
        this.businessID = UUID.randomUUID();
    }

    public long getId() {
        return id;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public de.hskl.itanalyst.addressauthorcartpublisherservice.domain.model.BookEntity getItem() {
        return item;
    }

    public void setItem(BookEntity item) {
        this.item = item;
    }

    public CartEntity getCart() {
        return cart;
    }

    public void setCart(CartEntity cart) {
        if (null == cart) return;
        if (null != this.cart) {
            this.cart.removeItem(this);
            this.cart = null;
        }
        this.cart = cart;

        cart.getItems().add(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CartItemEntity that = (CartItemEntity) o;
        return businessID.equals(that.businessID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(businessID);
    }
}
