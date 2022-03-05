package de.hskl.itanalyst.suchservice.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.NaturalId;
import springfox.documentation.annotations.ApiIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "Book")
@ApiIgnore
public class BookEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private long id;
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private PublisherEntity publisher;
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private Set<AuthorEntity> authors;
    private String title;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "book", orphanRemoval = true)
    private BuchlagerEntity buchlagerEntity;

    @NaturalId
    private final UUID businessID;

    @PreRemove
    public void preRemove() {
        Set<AuthorEntity> tmp = new HashSet<>(authors);
        tmp.forEach(authorEntity -> {
            authorEntity.removeBook(this);
        });
        removePublisher(publisher);
        authors.clear();
    }

    public BookEntity() {
        this(null, new HashSet<>(1), "NOT SET", 0);
    }

    public BookEntity(PublisherEntity publisher, Set<AuthorEntity> authors, String title, long amount) {
        this.businessID = UUID.randomUUID();
        this.title = title;
        setPublisher(publisher);
        addAuthors(authors);
        this.buchlagerEntity = new BuchlagerEntity(this, amount);
    }

    public void setAmount(long newAmount) {
        if (null != this.buchlagerEntity && newAmount >= 0) {
            this.buchlagerEntity.setAmountInStock(newAmount);
        }
    }

    public long getAmount() {
        if (null != this.buchlagerEntity) {
            return this.buchlagerEntity.getAmountInStock();
        }
        return 0;
    }

    public BuchlagerEntity getBuchlagerEntity() {
        return buchlagerEntity;
    }

    public void removeBuchlagerEntity() {
        if (null != this.buchlagerEntity && this.buchlagerEntity.getBook().equals(this)) {
            this.buchlagerEntity.removeBook();
        }
        this.buchlagerEntity = null;
    }

    public void setBuchlagerEntity(BuchlagerEntity buchlagerEntity) {
        if (null == buchlagerEntity) return;

        if (null != this.buchlagerEntity) {
            this.buchlagerEntity.removeBook();
            this.buchlagerEntity = null;
        }
        this.buchlagerEntity = buchlagerEntity;
        if (null == this.buchlagerEntity.getBook()) {
            this.buchlagerEntity.setBook(this);
        }
    }

    @ApiModelProperty(required = false)
    public long getId() {
        return id;
    }

    @ApiModelProperty(required = true)
    public PublisherEntity getPublisher() {
        return publisher;
    }

    @ApiModelProperty(required = true)
    public void setPublisher(PublisherEntity publisher) {
        if (null == publisher) return;

        if (null != this.publisher) {
            this.publisher.removeBook(this);
        }

        this.publisher = publisher;

        if (!publisher.getBooks().contains(this)) {
            publisher.addBook(this);
        }
    }

    public void removePublisher(PublisherEntity publisher) {
        if (null != this.publisher && this.publisher.equals(publisher)) {
            this.publisher.removeBook(this);
            this.publisher = null;
        }
    }

    @ApiModelProperty(required = true)
    public Set<AuthorEntity> getAuthors() {
        if (null == authors) {
            authors = new HashSet<>(1);
        }

        return authors;
    }

    @ApiModelProperty(required = true)
    public void addAuthors(Set<AuthorEntity> authors) {
        if (null == authors) return;
        if (authors.isEmpty() && null == this.authors) {
            this.authors = new HashSet<>(1);
            return;
        }
        authors.forEach(this::addAuthor);
    }

    public void removeAuthor(AuthorEntity authorEntity) {
        if (null != authors && authors.contains(authorEntity)) {
            authors.remove(authorEntity);
            authorEntity.removeBook(this);
        }
    }

    public void updateAuthors(Set<AuthorEntity> authors) {
        if (null == authors) return;

        // dirty!
        this.authors.forEach(authorsEntity -> {
            authorsEntity.removeBook(this);
        });
        this.authors.clear();

        if (authors.isEmpty()) {
            this.authors = new HashSet<>(1);
            return;
        }

        authors.forEach(this::addAuthor);
    }

    @ApiModelProperty(required = true)
    public void addAuthor(AuthorEntity author) {
        if (null == this.authors) {
            this.authors = new HashSet<>(1);
        }

        if (!this.authors.contains(author)) {
            this.authors.add(author);

            if (!author.getBooks().contains(this)) {
                author.addBook(this);
            }
        }
    }

    @ApiModelProperty(required = true)
    public String getTitle() {
        return title;
    }

    @ApiModelProperty(required = true)
    public void setTitle(String title) {
        this.title = title;
    }

    public String toString() {
        return this.title + " (" + ((this.publisher != null) ? this.publisher.getName() : "NO PUBLISHER") + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BookEntity that = (BookEntity) o;
        return businessID.equals(that.businessID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(businessID);
    }
}
