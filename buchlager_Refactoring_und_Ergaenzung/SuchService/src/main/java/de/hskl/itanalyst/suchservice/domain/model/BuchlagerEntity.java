package de.hskl.itanalyst.suchservice.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.NaturalId;
import springfox.documentation.annotations.ApiIgnore;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "BuchlagerEntity")
@ApiIgnore
public class BuchlagerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private long id;

    private long amountInStock;

    @NaturalId
    private final UUID businessID;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "BOOK_ID")
    private BookEntity book;

    public BuchlagerEntity() {
        this(null, 0);
    }

    public BuchlagerEntity(BookEntity book, long amountInStock) {
        businessID = UUID.randomUUID();
        setBook(book);
        this.amountInStock = amountInStock;
    }

    public void removeBook() {
        this.book = null;
    }

    public BookEntity getBook() {
        return book;
    }

    public long getAmountInStock() {
        return amountInStock;
    }

    public void setAmountInStock(long amountInStock) {
        this.amountInStock = amountInStock;
    }

    public void setBook(BookEntity bookEntity) {
        if (null != this.book && !this.book.equals(bookEntity)) {
            this.book.removeBuchlagerEntity();
        }
        this.book = bookEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BuchlagerEntity that = (BuchlagerEntity) o;
        return businessID.equals(that.businessID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(businessID);
    }
}
