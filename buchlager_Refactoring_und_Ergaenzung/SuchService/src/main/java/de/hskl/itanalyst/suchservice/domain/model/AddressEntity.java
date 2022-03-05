package de.hskl.itanalyst.suchservice.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.NaturalId;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "Address")
public class AddressEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private long id;
    @ApiModelProperty(hidden = true)
    @NonNull
    @NaturalId
    private String city;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "address")
    private final Set<PublisherEntity> publishers;

    public AddressEntity() {
        this("NOTSET");
    }

    public AddressEntity(String city) {
        this.city = city;
        this.publishers = new HashSet<>(1);
    }

    public void removePublisher(PublisherEntity publisher) {
        if (null == publisher) return;
        if (null == this.publishers || !this.publishers.contains(publisher)) return;

        this.publishers.remove(publisher);
        if (null != publisher.getAddress() && publisher.getAddress().equals(this)) {
            publisher.removeAddress();
        }
    }

    public Set<PublisherEntity> getPublishers() {
        return this.publishers;
    }

    public void addPublisher(PublisherEntity publisherEntity) {
        if (null == this.publishers) return;

        if (!this.publishers.contains(publisherEntity)) {
            this.publishers.add(publisherEntity);

            if (null != publisherEntity.getAddress() && !publisherEntity.getAddress().equals(this)) {
                publisherEntity.setAddress(this);
            }
        }
    }

    @ApiModelProperty(required = false, hidden = true)
    public long getId() {
        return id;
    }

    @ApiModelProperty(required = true, hidden = true)
    public String getCity() {
        return city;
    }

    @ApiModelProperty(required = true, hidden = true)
    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AddressEntity that = (AddressEntity) o;
        return city.equals(that.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city);
    }
}
