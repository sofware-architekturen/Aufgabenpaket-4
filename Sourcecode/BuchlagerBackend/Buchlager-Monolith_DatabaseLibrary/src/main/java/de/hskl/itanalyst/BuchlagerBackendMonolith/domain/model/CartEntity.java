package de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "CartEntity")
public class CartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private long id;

    @NaturalId
    private String sessionId;

    private LocalDateTime validUntil;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "cart")
    private Set<CartItemEntity> items;

    public CartEntity() {
        this(UUID.randomUUID().toString(), LocalDateTime.now().plus(1, ChronoUnit.MINUTES), new HashSet<>(1));
    }

    public CartEntity(String sessionId, LocalDateTime validUntil, Set<CartItemEntity> items) {
        this.sessionId = sessionId;
        this.validUntil = validUntil;
        addItems(items);
    }

    public long getId() {
        return id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }

    public Set<CartItemEntity> getItems() {
        return items;
    }

    public void addItems(Set<CartItemEntity> items) {
        if (null == items) return;
        if (items.isEmpty() && (null == this.items)) {
            this.items = new HashSet<>(1);
            return;
        }

        items.forEach(this::addItem);
    }

    public void addItem(CartItemEntity entity) {
        if (null == entity) return;

        if (null == this.items) {
            this.items = new HashSet<>(1);
        }

        this.items.add(entity);

        if (null == entity.getCart() || !entity.getCart().equals(this)) {
            entity.setCart(this);
        }
    }

    public void removeItem(CartItemEntity entity) {
        if (null != this.items) {
            this.items.remove(entity);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CartEntity that = (CartEntity) o;
        return sessionId.equals(that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId);
    }
}
