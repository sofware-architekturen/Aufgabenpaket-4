package de.hskl.itanalyst.suchservice.domain.converter;

import de.hskl.itanalyst.suchservice.domain.dto.Book.BookDTO;
import de.hskl.itanalyst.suchservice.domain.dto.Book.BookLightDTO;
import de.hskl.itanalyst.suchservice.domain.dto.Book.BookUpdateDTO;
import de.hskl.itanalyst.suchservice.domain.model.AuthorEntity;
import de.hskl.itanalyst.suchservice.domain.model.BookEntity;
import de.hskl.itanalyst.suchservice.domain.model.PublisherEntity;
import de.hskl.itanalyst.suchservice.repository.AuthorRepository;
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
public class BookConverters {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @PostConstruct
    public void postConstruct() {
        modelMapper.addConverter(bookEntityBookDTOConverter);
        modelMapper.addConverter(bookUpdateDTOBookEntityConverter);
        modelMapper.addConverter(bookEntityToBookLightDTOConverter);
    }

    private final Converter<BookEntity, BookDTO> bookEntityBookDTOConverter = new AbstractConverter<BookEntity, BookDTO>() {
        @Override
        protected BookDTO convert(BookEntity bookEntity) {
            final BookDTO bookDTO = new BookDTO();
            bookDTO.id = bookEntity.getId();
            bookDTO.authors = bookEntity.getAuthors().stream().map(AuthorEntity::toString).collect(Collectors.toSet());
            bookDTO.title = bookEntity.getTitle();
            bookDTO.PublisherAddressCity = bookEntity.getPublisher().getAddress().getCity();
            bookDTO.PublisherName = bookEntity.getPublisher().getName();
            bookDTO.amountInStock = bookEntity.getAmount();
            return bookDTO;
        }
    };

    private final Converter<BookEntity, BookLightDTO> bookEntityToBookLightDTOConverter = new AbstractConverter<BookEntity, BookLightDTO>() {
        @Override
        protected BookLightDTO convert(BookEntity bookEntity) {
            final BookLightDTO bookLightDTO = new BookLightDTO();
            bookLightDTO.id = bookEntity.getId();
            bookLightDTO.title = bookEntity.getTitle();
            bookLightDTO.amountInStock = bookEntity.getAmount();
            bookLightDTO.authors = bookEntity.getAuthors().stream().map(AuthorEntity::toString).collect(Collectors.toSet());
            return bookLightDTO;
        }
    };

    private final Converter<BookUpdateDTO, BookEntity> bookUpdateDTOBookEntityConverter = new AbstractConverter<BookUpdateDTO, BookEntity>() {
        @Override
        protected BookEntity convert(BookUpdateDTO updatedBookEntity) {
            if (null != updatedBookEntity.id) { // update case
                final Optional<BookEntity> bookEntity = bookRepository.findById(updatedBookEntity.id);
                if (bookEntity.isPresent()) {
                    if (null != updatedBookEntity.title && !updatedBookEntity.title.trim().isBlank()) {
                        bookEntity.get().setTitle(updatedBookEntity.title);
                    }

                    if (null != updatedBookEntity.publisherId) {
                        final Optional<PublisherEntity> publisherEntity = publisherRepository.findById(updatedBookEntity.publisherId);
                        if (publisherEntity.isPresent()) {
                            bookEntity.get().setPublisher(publisherEntity.get());
                        } else {
                            throw new IllegalArgumentException();
                        }
                    }

                    if (null != updatedBookEntity.authorIds && !updatedBookEntity.authorIds.isEmpty()) {
                        final Set<AuthorEntity> newAuthors = new HashSet<>(updatedBookEntity.authorIds.size());
                        updatedBookEntity.authorIds.stream().forEach(authorId -> {
                            final Optional<AuthorEntity> authorEntity = authorRepository.findById(authorId);
                            if (authorEntity.isPresent()) {
                                newAuthors.add(authorEntity.get());
                            } else {
                                throw new IllegalArgumentException();
                            }
                        });

                        bookEntity.get().updateAuthors(newAuthors);
                    }

                    if (null != updatedBookEntity.amountInStock) {
                        bookEntity.get().setAmount(Math.max(0L, updatedBookEntity.amountInStock));
                    }

                    return bookEntity.get();
                } else { // wrong id supplied!
                    throw new IllegalArgumentException("Id not valid - can't update the entity...");
                }
            } else { // new book case
                if (null == updatedBookEntity.title || updatedBookEntity.title.trim().isBlank() || null == updatedBookEntity.authorIds || null == updatedBookEntity.publisherId) {
                    throw new IllegalArgumentException("title, author or publisher ids wrong");
                }

                final BookEntity newBookEntity = new BookEntity();
                newBookEntity.setTitle(updatedBookEntity.title.trim());

                final Optional<PublisherEntity> publisherEntity = publisherRepository.findById(updatedBookEntity.publisherId);
                if (publisherEntity.isPresent()) {
                    newBookEntity.setPublisher(publisherEntity.get());
                } else {
                    throw new IllegalArgumentException("publisher not found");
                }

                updatedBookEntity.authorIds.forEach(authorId -> {
                    final Optional<AuthorEntity> authorEntity = authorRepository.findById(authorId);
                    if (authorEntity.isPresent()) {
                        newBookEntity.addAuthor(authorEntity.get());
                    } else {
                        throw new IllegalArgumentException("author not found");
                    }
                });

                if (null != updatedBookEntity.amountInStock) {
                    newBookEntity.setAmount(Math.max(0L, updatedBookEntity.amountInStock));
                } else {
                    newBookEntity.setAmount(0);
                }

                return newBookEntity;
            }
        }
    };
}
