package de.hskl.itanalyst.BuchlagerBackendMonolith.PublisherTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Publisher.PublisherDTO;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.dto.Publisher.PublisherUpdateDTO;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.AddressEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.AuthorEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.BookEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.domain.model.PublisherEntity;
import de.hskl.itanalyst.BuchlagerBackendMonolith.repository.PublisherRepository;
import de.hskl.itanalyst.BuchlagerBackendMonolith.service.IPublisherService;
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

import java.util.*;
import java.util.stream.StreamSupport;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PublisherAPITests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelMapperUtils modelMapperUtils;

    @MockBean
    private IPublisherService publisherService;

    @SpyBean
    private ModelMapper modelMapper;

    @MockBean
    private PublisherRepository publisherRepository;

    private final AuthorEntity authorEntity = new AuthorEntity("Max", "Mustermann", new HashSet<>(1));
    private final AddressEntity addressEntity = new AddressEntity("musterhausen");
    private final PublisherEntity publisherEntity = new PublisherEntity(addressEntity, new HashSet<>(1), "Musterverlag");
    private final BookEntity bookEntity = new BookEntity(publisherEntity, Set.of(authorEntity), "Mustertitel", 10);

    @BeforeEach
    public void setUp() {
        Mockito.reset(modelMapper);
        Spliterator<PublisherEntity> spliterator = Arrays.spliterator(new PublisherEntity[]{publisherEntity});
        Iterable<PublisherEntity> publisherEntityIterable = new Iterable<PublisherEntity>() {
            @Override
            public Spliterator<PublisherEntity> spliterator() {
                return spliterator;
            }

            @Override
            public Iterator<PublisherEntity> iterator() {
                return new Iterator<PublisherEntity>() {
                    private boolean hasNext = true;

                    @Override
                    public boolean hasNext() {
                        return hasNext;
                    }

                    @Override
                    public PublisherEntity next() {
                        try {
                            return publisherEntity;
                        } finally {
                            hasNext = false;
                        }
                    }
                };
            }
        };

        Mockito.when(publisherService.getPublisherById(0L)).thenReturn(Optional.of(publisherEntity));
        Mockito.when(publisherService.getPublisherById(1L)).thenReturn(Optional.empty());
        Mockito.when(publisherService.getAllPublisherAsStream()).thenReturn(StreamSupport.stream(publisherEntityIterable.spliterator(), false));
        Mockito.when(publisherService.addOrUpdatePublisher(Mockito.any())).thenReturn(publisherEntity);
        Mockito.when(publisherService.deletePublisher(0L)).thenReturn(Boolean.TRUE);
        Mockito.when(publisherService.deletePublisher(1L)).thenReturn(Boolean.FALSE);
    }

    @Test
    public void getPublisherById_ShouldReturnPublisher() throws Exception {
        this.mockMvc.perform(get("/api/publisher/0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(new ObjectMapper().writeValueAsString(modelMapper.map(publisherEntity, PublisherDTO.class))));
    }

    @Test
    public void getPublisherByWrongId_ShouldReturnNotFoundPublisher() throws Exception {
        this.mockMvc.perform(get("/api/publisher/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content()
                        .string(""));
    }

    @Test
    public void getAllPublishers_ShouldReturnPublisherStream() throws Exception {
        this.mockMvc.perform(get("/api/publisher"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(new ObjectMapper().writeValueAsString(modelMapperUtils.mapAll(List.of(publisherEntity), PublisherDTO.class))));
    }

    @Test
    public void getAllPublishersNoPublishers_ShouldReturnNoContentStatus() throws Exception {

        Mockito.when(publisherService.getAllPublisherAsStream()).thenReturn(new ArrayList<PublisherEntity>().stream());

        this.mockMvc.perform(get("/api/publisher"))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(content()
                        .string(""));
    }

    @Test
    public void putPublisher_ShouldReturnPublisherStream() throws Exception {
        Mockito.when(publisherRepository.findById(0L)).thenReturn(Optional.of(publisherEntity));

        PublisherUpdateDTO updatedPublisher = new PublisherUpdateDTO();
        updatedPublisher.id = 0L;

        this.mockMvc.perform(put("/api/publisher").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(modelMapper.map(updatedPublisher, PublisherUpdateDTO.class))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .json(new ObjectMapper().writeValueAsString(modelMapper.map(publisherEntity, PublisherDTO.class))));
    }

    @Test
    public void putPublisherException_ShouldReturnUnprocessableEntityStatus() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("demo")).when(publisherRepository).findById(0L);

        PublisherUpdateDTO updatedPublisher = new PublisherUpdateDTO();
        updatedPublisher.id = 0L;

        this.mockMvc.perform(put("/api/publisher").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(modelMapper.map(updatedPublisher, PublisherUpdateDTO.class))))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content()
                        .string(""));
    }

    @Test
    public void deletePublisher_ShouldReturnTrue() throws Exception {
        this.mockMvc.perform(delete("/api/publisher/0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .string(Boolean.TRUE.toString()));
    }

    @Test
    public void deletePublisherWrongId_ShouldReturnFalse() throws Exception {
        this.mockMvc.perform(delete("/api/publisher/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content()
                        .string(""));
    }
}
