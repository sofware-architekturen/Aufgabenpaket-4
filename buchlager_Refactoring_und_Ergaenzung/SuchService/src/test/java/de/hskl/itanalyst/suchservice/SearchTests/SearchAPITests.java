package de.hskl.itanalyst.suchservice.SearchTests;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.hskl.itanalyst.suchservice.domain.dto.Address.AddressDTO;
import de.hskl.itanalyst.suchservice.domain.dto.Author.AuthorDTO;
import de.hskl.itanalyst.suchservice.domain.dto.Book.BookLightDTO;
import de.hskl.itanalyst.suchservice.domain.dto.Publisher.PublisherDTO;
import de.hskl.itanalyst.suchservice.domain.dto.Search.MultiSearchResultDTO;
import de.hskl.itanalyst.suchservice.domain.model.AddressEntity;
import de.hskl.itanalyst.suchservice.domain.model.AuthorEntity;
import de.hskl.itanalyst.suchservice.domain.model.BookEntity;
import de.hskl.itanalyst.suchservice.domain.model.PublisherEntity;
import de.hskl.itanalyst.suchservice.service.ISearchService;
import de.hskl.itanalyst.suchservice.utils.ModelMapperUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SearchAPITests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelMapperUtils modelMapperUtils;

    @MockBean
    private ISearchService searchService;

    @SpyBean
    private ModelMapper modelMapper;

    private final AuthorEntity authorEntity = new AuthorEntity("Max", "Mustermann", new HashSet<>(1));
    private final AddressEntity addressEntity = new AddressEntity("musterhausen");
    private final PublisherEntity publisherEntity = new PublisherEntity(addressEntity, new HashSet<>(1), "Musterverlag");
    private final BookEntity bookEntity = new BookEntity(publisherEntity, Set.of(authorEntity), "Mustertitel", 10);

    private MultiSearchResultDTO multiSearchResultDTO;

    @BeforeEach
    public void setUp() {
        Mockito.reset(modelMapper);
        Mockito.when(searchService.searchAuthorsForString(Mockito.anyString())).thenReturn(List.of(authorEntity));
        Mockito.when(searchService.searchPublishersForString(Mockito.anyString())).thenReturn(List.of(publisherEntity));
        Mockito.when(searchService.searchBooksForString(Mockito.anyString())).thenReturn(List.of(bookEntity));
        Mockito.when(searchService.searchAddressesForString(Mockito.anyString())).thenReturn(List.of(addressEntity));

        multiSearchResultDTO = new MultiSearchResultDTO();
        multiSearchResultDTO.addresses = Set.of(modelMapper.map(addressEntity, AddressDTO.class));
        multiSearchResultDTO.authors = Set.of(modelMapper.map(authorEntity, AuthorDTO.class));
        multiSearchResultDTO.books = Set.of(modelMapper.map(bookEntity, BookLightDTO.class));
        multiSearchResultDTO.publishers = Set.of(modelMapper.map(publisherEntity, PublisherDTO.class));
    }

    @Test
    public void getSearchMultiProperty_ShouldReturnFindings() throws Exception {
        this.mockMvc.perform(get("/api/search/all/test"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(new ObjectMapper().writeValueAsString(multiSearchResultDTO)));
    }

    @Test
    public void getSearchMultiPropertyNoResult_ShouldReturnNotFoundStatus() throws Exception {
        Mockito.when(searchService.searchAuthorsForString(Mockito.anyString())).thenReturn(List.of());
        Mockito.when(searchService.searchPublishersForString(Mockito.anyString())).thenReturn(List.of());
        Mockito.when(searchService.searchBooksForString(Mockito.anyString())).thenReturn(List.of());
        Mockito.when(searchService.searchAddressesForString(Mockito.anyString())).thenReturn(List.of());

        this.mockMvc.perform(get("/api/search/all/test"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    public void getSearchMultiPropertyException_ShouldReturnNotFoundStatus() throws Exception {
        Mockito.when(searchService.searchAuthorsForString(Mockito.anyString())).thenThrow(new IllegalArgumentException("test"));

        this.mockMvc.perform(get("/api/search/all/test"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    public void getSearchBook_ShouldReturnBook() throws Exception {
        this.mockMvc.perform(get("/api/search/books/test"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(modelMapperUtils.mapAll(List.of(bookEntity), BookLightDTO.class))));
    }

    @Test
    public void getSearchNotExistingBook_ShouldReturnNotFoundStatus() throws Exception {
        Mockito.when(searchService.searchBooksForString(Mockito.anyString())).thenReturn(List.of());

        this.mockMvc.perform(get("/api/search/books/test"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    public void getSearchPublisher_ShouldReturnPublisher() throws Exception {
        this.mockMvc.perform(get("/api/search/publishers/test"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(modelMapperUtils.mapAll(List.of(publisherEntity), PublisherDTO.class))));
    }

    @Test
    public void getSearchNotExistingPublisher_ShouldReturnNotFoundStatus() throws Exception {
        Mockito.when(searchService.searchPublishersForString(Mockito.anyString())).thenReturn(List.of());

        this.mockMvc.perform(get("/api/search/publishers/test"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    public void getSearchAuthor_ShouldReturnAuthor() throws Exception {
        this.mockMvc.perform(get("/api/search/authors/test"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(modelMapperUtils.mapAll(List.of(authorEntity), AuthorDTO.class))));
    }

    @Test
    public void getSearchNotExistingAuthor_ShouldReturnNotFoundStatus() throws Exception {
        Mockito.when(searchService.searchAuthorsForString(Mockito.anyString())).thenReturn(List.of());

        this.mockMvc.perform(get("/api/search/authors/test"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    @Test
    public void getSearchAddress_ShouldReturnAddress() throws Exception {
        this.mockMvc.perform(get("/api/search/addresses/test"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(modelMapperUtils.mapAll(List.of(addressEntity), AddressDTO.class))));
    }

    @Test
    public void getSearchNotExistingAddress_ShouldReturnNotFoundStatus() throws Exception {
        Mockito.when(searchService.searchAddressesForString(Mockito.anyString())).thenReturn(List.of());

        this.mockMvc.perform(get("/api/search/addresses/test"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }
}
