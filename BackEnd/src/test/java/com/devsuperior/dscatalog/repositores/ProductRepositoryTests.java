package com.devsuperior.dscatalog.repositores;

import com.devsuperior.dscatalog.repositores.entities.Product;
import com.devsuperior.dscatalog.repositores.repositories.ProductRepository;
import com.devsuperior.dscatalog.repositores.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
public class ProductRepositoryTests {

   @Autowired
    private ProductRepository repository;

    private Long existingId;
    private Long nonExistId;
    private Long countTotalProduct;

    @BeforeEach
    void setUp() throws Exception{
        existingId = 1L;
        nonExistId = 1000L;
        countTotalProduct = 25L;
    }

    @Test
    public void saveShouldPersistWithAutoIncrementWhenIdIsNull(){
        Product product = Factory.createProduct();
        product.setId(null);
        product = repository.save(product);

        Assertions.assertNotNull(product.getId());
        Assertions.assertEquals(countTotalProduct + 1,product.getId());
    }

    @Test
    public void findByIdShouldFindExistId(){

        Optional<Product> result = repository.findById(existingId);
        Assertions.assertTrue(result.isPresent());

    }

}
