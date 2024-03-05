package com.devsuperior.dscatalog.repositores.services;

import com.devsuperior.dscatalog.repositores.dto.CategoryDTO;
import com.devsuperior.dscatalog.repositores.entities.Category;
import com.devsuperior.dscatalog.repositores.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositores.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.repositores.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAllPaged(Pageable pageable) {
        Page<Category> listPage = repository.findAll(pageable);
        return listPage.map(x -> new CategoryDTO(x));
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        Optional<Category> obj = repository.findById(id);
        Category entity = obj.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado!"));
        return new CategoryDTO(entity);
    }

    @Transactional
    public CategoryDTO insert(CategoryDTO dto) {
        Category entity = new Category();
        entity.setName(dto.getName());
        entity = repository.save(entity);
        return new CategoryDTO(entity);
    }

    @Transactional
    public void update(Long id, CategoryDTO objDTO) {
        try {
            Category entity = repository.getReferenceById(id);
            updateData(entity, objDTO);
            repository.save(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Usuário não encontrado: " + id);
        }
    }

    private void updateData(Category entity, CategoryDTO obj) {
        entity.setName(obj.getName());
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteById(long id) {
        if (!repository.existsById(id)){
            throw new ResourceNotFoundException("Recurso não encontrado: " + id);
        }try{
            repository.deleteById(id);
        }catch (DataIntegrityViolationException e){
            throw new DatabaseException("Falha de integridade referencial");
        }
    }
}
