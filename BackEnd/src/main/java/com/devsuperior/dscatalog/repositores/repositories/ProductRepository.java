package com.devsuperior.dscatalog.repositores.repositories;

import com.devsuperior.dscatalog.repositores.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
