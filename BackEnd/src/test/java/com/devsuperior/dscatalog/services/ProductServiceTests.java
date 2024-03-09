package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;


@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    @Mock
    CategoryRepository categoryRepository;

    private Long existId;
    private Long nonExistingId;
    private Long dependentId;

    private Long secondId;

    private Long referenceIdController;
    private Long categorId;
    private PageImpl<Product> page;
    private Product product;
    private Category category;
    private ProductDTO productDTO;

    @BeforeEach
    public void setUp() {
        existId = 1L;
        nonExistingId = 1000L;
        dependentId = 3L;
        categorId = 2L;
        product = Factory.createProduct();
        category = new Category();
        productDTO = Factory.createProductDTO();
        referenceIdController = 1L;
        secondId = 4L;


        page = new PageImpl<>(List.of(product));

        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page); // procurando todos

        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product); // salvando produto

        Mockito.when(categoryRepository.getReferenceById(ArgumentMatchers.any())).thenReturn(category); // referencia categoria

        Mockito.when(repository.getReferenceById(existId)).thenReturn(product); // referenciando product

        Mockito.when(repository.findById(existId)).thenReturn(Optional.of(product));

        Mockito.when(categoryRepository.findById(categorId)).thenReturn(Optional.of(category));

        Mockito.when(repository.getReferenceById(referenceIdController)).thenReturn(product);

        Mockito.when(repository.findById(existId)).thenReturn(Optional.empty());// veridicando se e nulo

        Mockito.when(repository.save(product)).thenReturn(product);

        Mockito.doNothing().when(repository).deleteById(existId);
        Mockito.doThrow(ResourceNotFoundException.class).when(repository).findById(existId);
        Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

        Mockito.when(categoryRepository.existsById(categorId)).thenReturn(true);
        Mockito.when(repository.existsById(existId)).thenReturn(true);
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
        Mockito.when(repository.existsById(dependentId)).thenReturn(true);
        Mockito.when(repository.existsById(secondId)).thenReturn(true);

    }


    @Test
    public void updateShouldReturnIdWhenExist() {

        Product entity = Factory.createProduct();

        Product prod = repository.getReferenceById(existId);

        Category cat = categoryRepository.getReferenceById(categorId);

        Product saveProduct = repository.save(entity);

        Assertions.assertNotNull(prod);
        Assertions.assertSame(product, saveProduct);
        Assertions.assertNotNull(cat);

    }

    @Test
    void insertShouldSaveNewProductDTO() {
        Product prod = Factory.createProduct();
        Mockito.when(repository.save(prod)).thenReturn(product);

        Product productSave = repository.save(prod);

        ProductDTO ins = service.insert(Factory.createProductDTO());

        Assertions.assertEquals(productSave, product);
        Assertions.assertNotNull(ins);

    }


    @Test
    public void findByIdShouldThrowResourceNotFoundException() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            ProductDTO finding = service.findById(nonExistingId);
            Assertions.assertNotNull(finding);
        });

    }

    @Test
    public void findByIdShouldReturnOneIdWhenExist() {
        long idFinded = 2;
        Mockito.when(repository.findById(idFinded)).thenReturn(Optional.of(product));
        Optional<Product> p = repository.findById(idFinded);


        Assertions.assertNotNull(service.findById(idFinded));
        Assertions.assertNotNull(p);


    }

    @Test
    public void findAllPagedShouldReturnPage() {


        Pageable pageable = PageRequest.of(0, 10);

        Page<ProductDTO> result = service.findAllPaged(pageable);

        Assertions.assertNotNull(result);
        Mockito.verify(repository).findAll(pageable);

    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
        Assertions.assertThrows(DatabaseException.class, () -> {
            service.deleteById(dependentId);
        });

    }

    @Test
    public void DeleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.deleteById(nonExistingId);
        });
    }

    @Test
    public void deleteShouldDoNothingWhenIdExists() {
        Assertions.assertDoesNotThrow(() -> {
            service.deleteById(existId);
        });
    }

}
