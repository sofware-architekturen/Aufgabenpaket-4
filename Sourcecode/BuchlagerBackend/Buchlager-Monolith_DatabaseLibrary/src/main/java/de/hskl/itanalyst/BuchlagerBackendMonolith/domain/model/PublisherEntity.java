package de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "Publisher")
public class PublisherEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private long id;
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "ADDRESS_ID")
    private AddressEntity address;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "publisher", orphanRemoval = true)
    private Set<BookEntity> books;
    @NaturalId
    private String name;

    public PublisherEntity() {
        this(null, new HashSet<>(1), "NOT SET");
    }

    public PublisherEntity(AddressEntity address, Set<BookEntity> books, String name) {
        this.name = name;
        setAddress(address);
        addBooks(books);
    }

    public PublisherEntity(AddressEntity address, String name) {
        this(address, new HashSet<>(1), name);
    }

    @PreRemove
    public void preRemove() {
        if (null != this.address) {
            this.address.removePublisher(this);
            this.address = null;
        }
    }

    @ApiModelProperty(required = false)
    public long getId() {
        return id;
    }

    @ApiModelProperty(required = true)
    public AddressEntity getAddress() {
        return address;
    }

    @ApiModelProperty(required = true)
    public void setAddress(AddressEntity address) {
        if (null != address) {
            removeAddress();
            this.address = address;

            if (null != address.getPublishers() && !address.getPublishers().contains(this)) {
                address.addPublisher(this);
            }
        }
    }

    public void removeAddress() {
        if (null == address) return;

        if (this.address.getPublishers().contains(this)) {
            this.address.removePublisher(this);
        }

        this.address = null;
    }

    @ApiModelProperty(required = false)
    public Set<BookEntity> getBooks() {
        if (null == this.books) {
            this.books = new HashSet<>(1);
        }

        return books;
    }

    @ApiModelProperty(required = false)
    public void addBooks(Set<BookEntity> books) {
        if (null == books) return;

        if (books.isEmpty() && (null == this.books)) {
            this.books = books;
            return;
        }

        books.forEach(this::addBook);
    }

    public void updateBooks(Set<BookEntity> books) {
        if (null == books) return;

        Set<BookEntity> tmp = new HashSet<>(this.books);
        // dirty!
        tmp.forEach(bookEntity -> {
            bookEntity.removePublisher(this);
        });
        this.books.clear();

        books.forEach(this::addBook);
    }

    public void removeBook(BookEntity book) {
        if (null != this.books && books.contains(book)) {
            books.remove(book);

            if (!book.getPublisher().equals(this)) {
                book.removePublisher(this);
            }
        }
    }

    @ApiModelProperty(required = false)
    public void addBook(BookEntity book) {
        if (null == book) return;

        if (null == this.books) {
            this.books = new HashSet<>(1);
        }

        if (!this.books.contains(book)) {
            this.books.add(book);

            if (null != book.getPublisher() && !book.getPublisher().equals(this)) {
                book.setPublisher(this);
            }
        }
    }

    @ApiModelProperty(required = true)
    public String getName() {
        return name;
    }

    @ApiModelProperty(required = true)
    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PublisherEntity that = (PublisherEntity) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
