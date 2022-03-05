package de.hskl.itanalyst.buchservice.service.impl;

import de.hskl.itanalyst.buchservice.domain.model.BookEntity;
import de.hskl.itanalyst.buchservice.repository.BookRepository;
import de.hskl.itanalyst.buchservice.service.IBookService;
import de.hskl.itanalyst.buchservice.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class BookService implements IBookService {
    @Autowired
    private BookRepository bookRepository;

//    @Autowired
//    private PublisherRepository publisherRepository;
//
//    @Autowired
//    private AuthorRepository authorRepository;

    private List<BookCoverConfiguration> bookCovers;
    private static final int NUM_OF_BOOKCOVERS = 2;

    @PostConstruct
    public void postConstruct() {
        // == Init book covers
        bookCovers = new ArrayList<>(NUM_OF_BOOKCOVERS);

        // Book 1
        BookCoverConfiguration bookCover = new BookCoverConfiguration(15, 40, 45, 40, 35, "classpath:Book1.png", 63);
        bookCovers.add(bookCover);
        // Book 2
        bookCover = new BookCoverConfiguration(12, 80, 55, 80, 60, "classpath:Book.png", 90);
        bookCovers.add(bookCover);
    }

    public Stream<BookEntity> getAllBooksAsStream() {
        final Iterable<BookEntity> allBooks = bookRepository.findAll();
        return StreamSupport.stream(allBooks.spliterator(), false);
    }

    public Optional<BookEntity> getBookById(final long bookId) {
        final Optional<BookEntity> bookEntity = bookRepository.findById(bookId);
        return bookEntity;
    }

    public BookEntity addOrUpdateBook(final BookEntity updatedBookEntity) {
        return bookRepository.save(updatedBookEntity);
    }

    public boolean deleteBook(final long bookId) {
        final Optional<BookEntity> bookEntity = bookRepository.findById(bookId);
        try {
            if (bookEntity.isPresent()) {
                bookRepository.delete(bookEntity.get());
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public Optional<BufferedImage> getBookCover(final long bookId) {
        final Optional<BookEntity> bookEntity = bookRepository.findById(bookId);
        try {
            if (bookEntity.isPresent()) {
                final BookCoverConfiguration coverConfiguration = bookCovers.get(Character.getNumericValue(bookEntity.get().getTitle().charAt(0)) % NUM_OF_BOOKCOVERS);
                final String authorsList = bookEntity.get().getAuthors().stream().map(Objects::toString).collect(Collectors.joining(", "));
                return Optional.of(createCoverImage(coverConfiguration, bookEntity.get().getTitle(), authorsList));
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static BufferedImage createCoverImage(final BookCoverConfiguration bookCoverConfiguration, final String title, final String author) throws IOException {
        final BufferedImage bufferedImage = ImageIO.read(ResourceUtils.getFile(bookCoverConfiguration.url));
        final Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setColor(Color.BLACK);
        graphics.setFont(new Font("Arial", Font.PLAIN, bookCoverConfiguration.fontsize));
        graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_DITHERING,
                RenderingHints.VALUE_DITHER_ENABLE);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);
        FontMetrics fm = graphics.getFontMetrics();

        List<String> textList = StringUtils.wrap(title, fm, bufferedImage.getWidth() - bookCoverConfiguration.widthOffset);
        for (int i = 0; i < textList.size(); i++) {
            graphics.drawString(textList.get(i), bookCoverConfiguration.xTitle, bookCoverConfiguration.yTitle + fm.getHeight() * i);
        }

        textList = StringUtils.wrap(author, fm, bufferedImage.getWidth() - bookCoverConfiguration.widthOffset);
        for (int i = 0; i < textList.size(); i++) {
            graphics.drawString(textList.get(textList.size() - 1 - i), bookCoverConfiguration.xAuthor, bufferedImage.getHeight() - bookCoverConfiguration.yAuthor - fm.getHeight() * i);
        }
        graphics.dispose();

        return bufferedImage;
    }

    private static class BookCoverConfiguration {
        int fontsize;
        int xTitle;
        int yTitle;
        int xAuthor;
        int yAuthor;
        int widthOffset;
        String url;

        public BookCoverConfiguration(int fontsize, int xTitle, int yTitle, int xAuthor, int yAuthor, String url, int widthOffset) {
            this.fontsize = fontsize;
            this.xTitle = xTitle;
            this.yTitle = yTitle;
            this.xAuthor = xAuthor;
            this.yAuthor = yAuthor;
            this.url = url;
            this.widthOffset = widthOffset;
        }
    }
}
