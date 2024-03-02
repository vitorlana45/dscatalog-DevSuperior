package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(PageRequest pageRequest) {
        Page<Product> listPage = repository.findAll(pageRequest);
        return listPage.map(x -> new ProductDTO(x));
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Optional<Product> obj = repository.findById(id);
        Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado!"));
        return new ProductDTO(entity, entity.getCategories());
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto) {
        Product entity = new Product();
       // entity.setName(dto.getName());
        entity = repository.save(entity);
        return new ProductDTO(entity);
    }

    @Transactional
    public void update(Long id, ProductDTO objDTO) {
        try {
            Product entity = repository.getReferenceById(id);
           // updateData(entity, objDTO);
            repository.save(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Usuário não encontrado: " + id);
        }
    }

    private void updateData(Product entity, ProductDTO obj) {
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
