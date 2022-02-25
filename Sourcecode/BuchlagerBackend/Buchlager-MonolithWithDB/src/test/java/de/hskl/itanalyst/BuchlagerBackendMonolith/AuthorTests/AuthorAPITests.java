package de.hskl.itanalyst.BuchlagerBackendMonolith.AuthorTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Author.AuthorDTO;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Author.AuthorUpdateDTO;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.AddressEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.AuthorEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.BookEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.PublisherEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.repository.AuthorRepository;
import de.hskl.itanalyst.BuchlagerBackendMonolith.service.IAuthorService;
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

import java.io.IOException;
import java.util.*;
import java.util.stream.StreamSupport;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthorAPITests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelMapperUtils modelMapperUtils;

    @MockBean
    private IAuthorService authorService;

    @SpyBean
    private ModelMapper modelMapper;

    @MockBean
    private AuthorRepository authorRepository;

    private final AuthorEntity authorEntity = new AuthorEntity("Max", "Mustermann", new HashSet<>(1));
    private final AddressEntity addressEntity = new AddressEntity("musterhausen");
    private final PublisherEntity publisherEntity = new PublisherEntity(addressEntity, new HashSet<>(1), "Musterverlag");
    private final BookEntity bookEntity = new BookEntity(publisherEntity, Set.of(authorEntity), "Mustertitel", 10);

    public AuthorAPITests() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        Mockito.reset(modelMapper);
        Spliterator<AuthorEntity> spliterator = Arrays.spliterator(new AuthorEntity[]{authorEntity});
        Iterable<AuthorEntity> authorEntityIterable = new Iterable<AuthorEntity>() {
            @Override
            public Spliterator<AuthorEntity> spliterator() {
                return spliterator;
            }

            @Override
            public Iterator<AuthorEntity> iterator() {
                return new Iterator<AuthorEntity>() {
                    private boolean hasNext = true;

                    @Override
                    public boolean hasNext() {
                        return hasNext;
                    }

                    @Override
                    public AuthorEntity next() {
                        try {
                            return authorEntity;
                        } finally {
                            hasNext = false;
                        }
                    }
                };
            }
        };

        Mockito.when(authorService.getAuthorById(0L)).thenReturn(Optional.of(authorEntity));
        Mockito.when(authorService.getAuthorById(1L)).thenReturn(Optional.empty());
        Mockito.when(authorService.getAllAuthorsAsStream()).thenReturn(StreamSupport.stream(authorEntityIterable.spliterator(), false));
        Mockito.when(authorService.addOrUpdateAuthor(Mockito.any())).thenReturn(authorEntity);
        Mockito.when(authorService.deleteAuthor(0L)).thenReturn(Boolean.TRUE);
        Mockito.when(authorService.deleteAuthor(1L)).thenReturn(Boolean.FALSE);
    }

    @Test
    public void getBookById_ShouldReturnBook() throws Exception {
        this.mockMvc.perform(get("/api/authors/0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(new ObjectMapper().writeValueAsString(modelMapper.map(authorEntity, AuthorDTO.class))));
    }

    @Test
    public void getBookByWrongId_ShouldReturnNotFoundBook() throws Exception {
        this.mockMvc.perform(get("/api/authors/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content()
                        .string(""));
    }

    @Test
    public void getAllBooks_ShouldReturnBookStream() throws Exception {
        this.mockMvc.perform(get("/api/authors"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(new ObjectMapper().writeValueAsString(modelMapperUtils.mapAll(List.of(authorEntity), AuthorDTO.class))));
    }

    @Test
    public void getAllBooksNoBooks_ShouldReturnNoContentStatus() throws Exception {

        Mockito.when(authorService.getAllAuthorsAsStream()).thenReturn(new ArrayList<AuthorEntity>().stream());

        this.mockMvc.perform(get("/api/authors"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(content()
                        .string(""));
    }

    @Test
    public void putBook_ShouldReturnBookStream() throws Exception {
        Mockito.when(authorRepository.findById(0L)).thenReturn(Optional.of(authorEntity));

        AuthorUpdateDTO updatedAuthor = new AuthorUpdateDTO();
        updatedAuthor.id = 0L;

        this.mockMvc.perform(put("/api/authors").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(modelMapper.map(updatedAuthor, AuthorUpdateDTO.class))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(new ObjectMapper().writeValueAsString(modelMapper.map(authorEntity, AuthorDTO.class))));
    }

    @Test
    public void putBookException_ShouldReturnUnprocessableEntityStatus() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("demo")).when(authorRepository).findById(0L);

        AuthorUpdateDTO updatedAuthor = new AuthorUpdateDTO();
        updatedAuthor.id = 0L;

        this.mockMvc.perform(put("/api/authors").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(modelMapper.map(updatedAuthor, AuthorUpdateDTO.class))))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content()
                        .string(""));
    }

    @Test
    public void deleteBook_ShouldReturnTrue() throws Exception {
        this.mockMvc.perform(delete("/api/authors/0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .string(Boolean.TRUE.toString()));
    }

    @Test
    public void deleteBookWrongId_ShouldReturnFalse() throws Exception {
        this.mockMvc.perform(delete("/api/authors/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content()
                        .string(""));
    }
}
