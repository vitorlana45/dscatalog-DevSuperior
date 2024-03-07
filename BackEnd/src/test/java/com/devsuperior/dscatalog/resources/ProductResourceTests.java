
package com.devsuperior.dscatalog.resources;

import com.devsuperior.dscatalog.repositores.DscatalogApplication;
import com.devsuperior.dscatalog.repositores.dto.ProductDTO;
import com.devsuperior.dscatalog.repositores.entities.Category;
import com.devsuperior.dscatalog.repositores.services.ProductService;
import com.devsuperior.dscatalog.repositores.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.repositores.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.repositores.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = DscatalogApplication.class)
@AutoConfigureMockMvc
public class ProductResourceTests {

    @Autowired
    private MockMvc mockMvc;

    private Long existingId;
    private Long nonExistingId;

    private Long idDataBaseIntegrity;

    @MockBean
    private ProductService service;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductDTO productDTO;
    private PageImpl<ProductDTO> page;


    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 2L;
        idDataBaseIntegrity = 3L;

        productDTO = Factory.createProductDTO();
        page = new PageImpl<>(List.of(productDTO));

        when(service.findAllPaged(any())).thenReturn(page);

        when(service.findById(existingId)).thenReturn(productDTO);
        when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        when(service.update(eq(existingId), any())).thenReturn(productDTO);
        when(service.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);

        doNothing().when(service).deleteById(existingId);
        doThrow(ResourceNotFoundException.class).when(service).deleteById(nonExistingId);
        doThrow(DatabaseException.class).when(service).deleteById(idDataBaseIntegrity);

        when(service.insert(any(ProductDTO.class))).thenReturn(productDTO);

    }

    @Test
    public void findAllShouldReturnPage() throws Exception {

        ResultActions result = mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk());
    }

    @Test
    void findByIdShouldReturnProductWhenIdExistis() throws Exception {
        ResultActions result = mockMvc.perform(get("/products/{id}", existingId).accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
        result.andExpect(jsonPath("$.price").exists());

    }

    @Test
    void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {

        ResultActions result = mockMvc.perform(get("/products/{id}", nonExistingId).accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions result = mockMvc.perform(put("/products/{id}", existingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());
    }

    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result = mockMvc.perform(put("/products/{id}", nonExistingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());
    }

    @Test
    public void deleteByIdShouldReturnNoContentWhenIdExists() throws Exception {

        ResultActions result = mockMvc.perform(delete("/products/{id}", existingId)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent());

    }

    @Test
    public void deleteByIdShouldReturnNotFoundWhenIdDoesExist() throws Exception {

        ResultActions result = mockMvc.perform(delete("/products/{id}", nonExistingId));

        result.andExpect(status().isNotFound());

    }

    @Test
    public void testInsertProduct() throws Exception {
        ProductDTO dto = Factory.createProductDTO();
        String jsonBody = objectMapper.writeValueAsString(dto);
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isCreated());
    }
}