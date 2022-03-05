package de.hskl.itanalyst.suchservice.domain.converter;

import de.hskl.itanalyst.suchservice.domain.dto.Book.BookLightDTO;
import de.hskl.itanalyst.suchservice.domain.dto.Publisher.PublisherDTO;
import de.hskl.itanalyst.suchservice.domain.dto.Publisher.PublisherUpdateDTO;
import de.hskl.itanalyst.suchservice.domain.model.AddressEntity;
import de.hskl.itanalyst.suchservice.domain.model.AuthorEntity;
import de.hskl.itanalyst.suchservice.domain.model.BookEntity;
import de.hskl.itanalyst.suchservice.domain.model.PublisherEntity;
import de.hskl.itanalyst.suchservice.repository.AddressRepository;
import de.hskl.itanalyst.suchservice.repository.BookRepository;
import de.hskl.itanalyst.suchservice.repository.PublisherRepository;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PublisherConverters {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @PostConstruct
    public void postConstruct() {
        modelMapper.addConverter(publisherEntityPublisherDTOConverter);
        modelMapper.addConverter(publisherUpdateDTOPublisherEntityConverter);
    }

    private final Converter<PublisherEntity, PublisherDTO> publisherEntityPublisherDTOConverter = new AbstractConverter<PublisherEntity, PublisherDTO>() {
        @Override
        protected PublisherDTO convert(PublisherEntity publisherEntity) {
            final PublisherDTO publisherDTO = new PublisherDTO();
            publisherDTO.id = publisherEntity.getId();
            publisherDTO.name = publisherEntity.getName();
            publisherDTO.books = publisherEntity.getBooks().stream().map(PublisherConverters.this::BookEntityToBookLightMapper).collect(Collectors.toSet());
            publisherDTO.city = publisherEntity.getAddress().getCity();
            return publisherDTO;
        }
    };

    private BookLightDTO BookEntityToBookLightMapper(final BookEntity bookEntity) {
        final BookLightDTO bookLightDTO = new BookLightDTO();
        bookLightDTO.id = bookEntity.getId();
        bookLightDTO.title = bookEntity.getTitle();
        bookLightDTO.authors = bookEntity.getAuthors().stream().map(AuthorEntity::toString).collect(Collectors.toSet());
        return bookLightDTO;
    }

    private final Converter<PublisherUpdateDTO, PublisherEntity> publisherUpdateDTOPublisherEntityConverter = new AbstractConverter<PublisherUpdateDTO, PublisherEntity>() {
        @Override
        protected PublisherEntity convert(PublisherUpdateDTO updatedPublisherEntity) {
            if (updatedPublisherEntity.id != null) { // entity to update
                final Optional<PublisherEntity> publisherEntity = publisherRepository.findById(updatedPublisherEntity.id);
                if (publisherEntity.isPresent()) {
                    if (updatedPublisherEntity.name != null && !updatedPublisherEntity.name.trim().isBlank()) {
                        publisherEntity.get().setName(updatedPublisherEntity.name);
                    }
                    if (updatedPublisherEntity.bookIds != null && !updatedPublisherEntity.bookIds.isEmpty()) {
                        final Set<BookEntity> newBookEnities = new HashSet<>(updatedPublisherEntity.bookIds.size());
                        updatedPublisherEntity.bookIds.forEach(bookId -> {
                            final Optional<BookEntity> bookEntity = bookRepository.findById(bookId);
                            if (bookEntity.isPresent()) {
                                newBookEnities.add(bookEntity.get());
                            } else {
                                throw new IllegalArgumentException("invalid book id");
                            }
                        });
                        publisherEntity.get().updateBooks(newBookEnities);
                    }
                    if (updatedPublisherEntity.addressId != null) {
                        final Optional<AddressEntity> addressEntity = addressRepository.findById(updatedPublisherEntity.addressId);
                        if (addressEntity.isPresent()) {
                            publisherEntity.get().setAddress(addressEntity.get());
                        } else {
                            throw new IllegalArgumentException("invalid address id");
                        }
                    }
                    return publisherEntity.get();
                } else { // wrong id supplied!
                    throw new IllegalArgumentException("Id not valid - can't update the entity...");
                }
            } else { // new entity
                if (updatedPublisherEntity.addressId == null) {
                    throw new IllegalArgumentException("invalid address id");
                }

                if (updatedPublisherEntity.name == null || updatedPublisherEntity.name.trim().isBlank()) {
                    throw new IllegalArgumentException("invalid name value");
                }

                final Optional<AddressEntity> addressEntity = addressRepository.findById(updatedPublisherEntity.addressId);
                if (addressEntity.isEmpty()) {
                    throw new IllegalArgumentException("invalid address id");
                }

                int numOfBookIds = 0;
                if (updatedPublisherEntity.bookIds != null) {
                    numOfBookIds = updatedPublisherEntity.bookIds.size();
                }
                final PublisherEntity newPublisherEntity = new PublisherEntity(addressEntity.get(), new HashSet<>(numOfBookIds), updatedPublisherEntity.name);

                if (numOfBookIds > 0) {
                    updatedPublisherEntity.bookIds.forEach(bookId -> {
                        final Optional<BookEntity> bookEntity = bookRepository.findById(bookId);
                        if (bookEntity.isPresent()) {
                            newPublisherEntity.addBook(bookEntity.get());
                        } else {
                            throw new IllegalArgumentException("invalid book id");
                        }
                    });
                }

                return newPublisherEntity;
            }
        }
    };
}
