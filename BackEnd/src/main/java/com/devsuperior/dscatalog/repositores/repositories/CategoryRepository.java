package com.devsuperior.dscatalog.repositores.repositories;

import com.devsuperior.dscatalog.repositores.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
