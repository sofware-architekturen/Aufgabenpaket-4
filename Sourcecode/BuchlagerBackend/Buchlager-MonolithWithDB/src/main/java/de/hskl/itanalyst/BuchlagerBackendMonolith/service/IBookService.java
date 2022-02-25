package de.hskl.itanalyst.BuchlagerBackendMonolith.service;

import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.BookEntity;

import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.stream.Stream;

public interface IBookService {
    Stream<BookEntity> getAllBooksAsStream();

    Optional<BookEntity> getBookById(final long bookId);

    BookEntity addOrUpdateBook(final BookEntity updatedBookEntity);

    boolean deleteBook(final long bookId);

    Optional<BufferedImage> getBookCover(final long bookId);
}
