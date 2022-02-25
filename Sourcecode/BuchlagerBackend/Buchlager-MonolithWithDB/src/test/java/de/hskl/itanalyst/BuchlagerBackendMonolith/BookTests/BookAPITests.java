package de.hskl.itanalyst.BuchlagerBackendMonolith.BookTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Book.BookDTO;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Book.BookUpdateDTO;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.AddressEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.AuthorEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.BookEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.PublisherEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.repository.BookRepository;
import de.hskl.itanalyst.BuchlagerBackendMonolith.service.IBookService;
import de.hskl.itanalyst.BuchlagerBackendMonolith.utils.ModelMapperUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.StreamSupport;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookAPITests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelMapperUtils modelMapperUtils;

    @MockBean
    private IBookService bookService;

    @SpyBean
    private ModelMapper modelMapper;

    @MockBean
    private BookRepository bookRepository;

    private final AuthorEntity authorEntity = new AuthorEntity("Max", "Mustermann", new HashSet<>(1));
    private final AddressEntity addressEntity = new AddressEntity("musterhausen");
    private final PublisherEntity publisherEntity = new PublisherEntity(addressEntity, new HashSet<>(1), "Musterverlag");
    private final BookEntity bookEntity = new BookEntity(publisherEntity, Set.of(authorEntity), "Mustertitel", 10);
    private final BufferedImage bufferedImage = ImageIO.read(ResourceUtils.getFile("classpath:Book.png"));

    public BookAPITests() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        Mockito.reset(modelMapper);
        Spliterator<BookEntity> spliterator = Arrays.spliterator(new BookEntity[]{bookEntity});
        Iterable<BookEntity> bookEntityIterable = new Iterable<BookEntity>() {
            @Override
            public Spliterator<BookEntity> spliterator() {
                return spliterator;
            }

            @Override
            public Iterator<BookEntity> iterator() {
                return new Iterator<BookEntity>() {
                    private boolean hasNext = true;

                    @Override
                    public boolean hasNext() {
                        return hasNext;
                    }

                    @Override
                    public BookEntity next() {
                        try {
                            return bookEntity;
                        } finally {
                            hasNext = false;
                        }
                    }
                };
            }
        };

        Mockito.when(bookService.getBookById(0L)).thenReturn(Optional.of(bookEntity));
        Mockito.when(bookService.getBookById(1L)).thenReturn(Optional.empty());
        Mockito.when(bookService.getAllBooksAsStream()).thenReturn(StreamSupport.stream(bookEntityIterable.spliterator(), false));
        Mockito.when(bookService.addOrUpdateBook(Mockito.any())).thenReturn(bookEntity);
        Mockito.when(bookService.deleteBook(0L)).thenReturn(Boolean.TRUE);
        Mockito.when(bookService.deleteBook(1L)).thenReturn(Boolean.FALSE);
    }

    @Test
    public void getBookById_ShouldReturnBook() throws Exception {
        this.mockMvc.perform(get("/api/books/0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(new ObjectMapper().writeValueAsString(modelMapper.map(bookEntity, BookDTO.class))));
    }

    @Test
    public void getBookByWrongId_ShouldReturnNotFoundBook() throws Exception {
        this.mockMvc.perform(get("/api/books/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content()
                        .string(""));
    }

    @Test
    public void getAllBooks_ShouldReturnBookStream() throws Exception {
        this.mockMvc.perform(get("/api/books"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(new ObjectMapper().writeValueAsString(modelMapperUtils.mapAll(List.of(bookEntity), BookDTO.class))));
    }

    @Test
    public void getAllBooksNoBooks_ShouldReturnNoContentStatus() throws Exception {

        Mockito.when(bookService.getAllBooksAsStream()).thenReturn(new ArrayList<BookEntity>().stream());

        this.mockMvc.perform(get("/api/books"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(content()
                        .string(""));
    }

    @Test
    public void putBook_ShouldReturnBookStream() throws Exception {
        Mockito.when(bookRepository.findById(0L)).thenReturn(Optional.of(bookEntity));

        BookUpdateDTO updatedBook = new BookUpdateDTO();
        updatedBook.id = 0L;

        this.mockMvc.perform(put("/api/books").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(modelMapper.map(updatedBook, BookUpdateDTO.class))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(new ObjectMapper().writeValueAsString(modelMapper.map(bookEntity, BookDTO.class))));
    }

    @Test
    public void putBookException_ShouldReturnUnprocessableEntityStatus() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("demo")).when(bookRepository).findById(0L);

        BookUpdateDTO updatedBook = new BookUpdateDTO();
        updatedBook.id = 0L;

        this.mockMvc.perform(put("/api/books").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(modelMapper.map(updatedBook, BookUpdateDTO.class))))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content()
                        .string(""));
    }

    @Test
    public void deleteBook_ShouldReturnTrue() throws Exception {
        this.mockMvc.perform(delete("/api/books/0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .string(Boolean.TRUE.toString()));
    }

    @Test
    public void deleteBookWrongId_ShouldReturnFalse() throws Exception {
        this.mockMvc.perform(delete("/api/books/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content()
                        .string(""));
    }

    @Test
    public void getBookCoverForId_ShouldReturnByteArray() throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        Mockito.when(bookService.getBookCover(0L)).thenReturn(Optional.of(bufferedImage));
        this.mockMvc.perform(get("/api/books/bookcover/0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .bytes(baos.toByteArray()));
    }

    @Test
    public void getBookCoverForWrongId_ShouldReturnNotFoundStatus() throws Exception {
        this.mockMvc.perform(get("/api/books/bookcover/0"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content()
                        .string(""));
    }

    @Test
    public void getBookCoverForIdWithException_ShouldReturnNotFoundStatus() throws Exception {
        final ByteArrayOutputStream baosMock = Mockito.mock(ByteArrayOutputStream.class);
        Mockito.when(baosMock.toByteArray()).thenThrow(new RuntimeException("demo"));
        BufferedImage image = Mockito.mock(BufferedImage.class);
        Mockito.when(bookService.getBookCover(0L)).thenReturn(Optional.of(image));

        this.mockMvc.perform(get("/api/books/bookcover/0"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content()
                        .string(""));
    }
}
