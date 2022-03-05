package de.hskl.itanalyst.addressauthorcartpublisherservice.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.*;

@ApiModel(description = "Die Beschreibung eines Authors.")
@Entity
@Table(name = "Author")
public class AuthorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private long id;
    private String firstName;
    private String lastName;
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "authors")
    private Set<BookEntity> books;

    @NaturalId
    private final UUID businessID;

    public AuthorEntity() {
        this("", "", new HashSet<>(1));
    }

    public AuthorEntity(String firstName, String lastName, Set<BookEntity> books) {
        this.firstName = firstName;
        this.lastName = lastName;
        addBooks(books);
        this.businessID = UUID.randomUUID();
    }

    public AuthorEntity(String firstName, String lastName) {
        this(firstName, lastName, Collections.EMPTY_SET);
    }

    @ApiModelProperty(required = false)
    public long getId() {
        return id;
    }

    @ApiModelProperty(required = true)
    public String getFirstName() {
        return firstName;
    }

    @ApiModelProperty(required = true)
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @ApiModelProperty(required = true)
    public String getLastName() {
        return lastName;
    }

    @ApiModelProperty(required = true)
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @ApiModelProperty(required = false)
    public Set<BookEntity> getBooks() {
        if (null == books) {
            books = new HashSet<>(1);
        }

        return books;
    }

    public void addBooks(final Set<BookEntity> books) {
        if (null == books) return;
        if (books.isEmpty() && (null == this.books)) {
            this.books = new HashSet<>(1);
            return;
        }

        books.forEach(this::addBook);
    }

    public void updateBooks(final Set<BookEntity> books) {
        if (null == books) return;
        Set<BookEntity> tmp = new HashSet<>(this.books);
        tmp.forEach(bookEntity -> {
            bookEntity.removeAuthor(this);
        });
        this.books.clear();

        if (books.isEmpty() && (null == this.books)) {
            this.books = new HashSet<>(1);
            return;
        }

        books.forEach(this::addBook);
    }

    public void removeBook(BookEntity bookEntity) {
        if (null != books && books.contains(bookEntity)) {
            books.remove(bookEntity);
            bookEntity.removeAuthor(this);
        }
    }

    @ApiModelProperty(required = false)
    public void addBook(BookEntity book) {
        if (null == this.books) {
            this.books = new HashSet<>(1);
        }

        if (!this.books.contains(book)) {
            this.books.add(book);

            if (!book.getAuthors().contains(this)) {
                book.addAuthor(this);
            }
        }
    }

    public String toString() {
        return this.firstName + " " + this.lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AuthorEntity that = (AuthorEntity) o;
        return businessID.equals(that.businessID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(businessID);
    }
}
