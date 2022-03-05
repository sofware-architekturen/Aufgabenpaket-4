package de.hskl.itanalyst.addressauthorcartpublisherservice.domain.converter;

import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.dto.Author.AuthorDTO;
import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.dto.Author.AuthorUpdateDTO;
import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.dto.Book.BookLightDTO;
import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.model.AuthorEntity;
import de.hskl.itanalyst.addressauthorcartpublisherservice.domain.model.BookEntity;
import de.hskl.itanalyst.addressauthorcartpublisherservice.repository.AuthorRepository;
import de.hskl.itanalyst.addressauthorcartpublisherservice.repository.BookRepository;
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
public class AuthorConverters {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @PostConstruct
    public void postConstruct() {
        modelMapper.addConverter(authorEntityAuthorDTOConverter);
        modelMapper.addConverter(authorUpdateDTOAuthorEntityConverter);
    }

    private final Converter<AuthorEntity, AuthorDTO> authorEntityAuthorDTOConverter = new AbstractConverter<AuthorEntity, AuthorDTO>() {
        @Override
        protected AuthorDTO convert(AuthorEntity authorEntity) {
            final AuthorDTO authorDTO = new AuthorDTO();
            authorDTO.id = authorEntity.getId();
            authorDTO.firstName = authorEntity.getFirstName();
            authorDTO.lastName = authorEntity.getLastName();
            authorDTO.books = authorEntity.getBooks().stream().map(AuthorConverters.this::BookEntityToBookLightMapper).collect(Collectors.toSet());
            return authorDTO;
        }
    };

    private BookLightDTO BookEntityToBookLightMapper(final BookEntity bookEntity) {
        final BookLightDTO bookLightDTO = new BookLightDTO();
        bookLightDTO.id = bookEntity.getId();
        bookLightDTO.title = bookEntity.getTitle();
        bookLightDTO.authors = bookEntity.getAuthors().stream().map(AuthorEntity::toString).collect(Collectors.toSet());
        return bookLightDTO;
    }

    private final Converter<AuthorUpdateDTO, AuthorEntity> authorUpdateDTOAuthorEntityConverter = new AbstractConverter<AuthorUpdateDTO, AuthorEntity>() {
        @Override
        protected AuthorEntity convert(AuthorUpdateDTO updatedAuthorEntity) {
            if (null != updatedAuthorEntity.id) { // update case
                final Optional<AuthorEntity> authorEntity = authorRepository.findById(updatedAuthorEntity.id);
                if (authorEntity.isPresent()) {
                    if (updatedAuthorEntity.firstName != null) {
                        authorEntity.get().setFirstName(updatedAuthorEntity.firstName);
                    }
                    if (updatedAuthorEntity.lastName != null) {
                        authorEntity.get().setLastName(updatedAuthorEntity.lastName);
                    }
                    if (null != updatedAuthorEntity.bookIds && !updatedAuthorEntity.bookIds.isEmpty()) {
                        final Set<BookEntity> newBookEnities = new HashSet<>(updatedAuthorEntity.bookIds.size());
                        updatedAuthorEntity.bookIds.forEach(bookId -> {
                            final Optional<BookEntity> bookEntity = bookRepository.findById(bookId);
                            if (bookEntity.isPresent()) {
                                newBookEnities.add(bookEntity.get());
                            } else {
                                throw new IllegalArgumentException("invalid book id");
                            }
                        });
                        authorEntity.get().updateBooks(newBookEnities);
                    }
                    return authorEntity.get();
                } else {
                    throw new IllegalArgumentException("Id not valid - can't update the entity...");
                }
            } else {
                if ((updatedAuthorEntity.firstName == null || updatedAuthorEntity.firstName.trim().isBlank()) ||
                        (updatedAuthorEntity.lastName == null || updatedAuthorEntity.lastName.trim().isBlank())) {
                    throw new IllegalArgumentException("first or last name blank");
                }

                int numOfBookIds = 0;
                if (updatedAuthorEntity.bookIds != null) {
                    numOfBookIds = updatedAuthorEntity.bookIds.size();
                }

                final AuthorEntity newAuthorEntity = new AuthorEntity(updatedAuthorEntity.firstName.trim(), updatedAuthorEntity.lastName.trim(), new HashSet<>(numOfBookIds));

                if (numOfBookIds > 0) {
                    updatedAuthorEntity.bookIds.forEach(bookId -> {
                        final Optional<BookEntity> bookEntity = bookRepository.findById(bookId);
                        if (bookEntity.isPresent()) {
                            newAuthorEntity.addBook(bookEntity.get());
                        } else {
                            throw new IllegalArgumentException("One of the book IDs wrong...");
                        }
                    });
                }

                return newAuthorEntity;
            }
        }
    };
}
