package com.herval.ecommtie.services;

import com.herval.ecommtie.model.entity.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface CategoriaService {
    Categoria save(Categoria categoria);

    Optional<Categoria> getById(Long id);

    void delete(Categoria categoria);

    Categoria update(Categoria updatingCategoria);

    Page<Categoria> find(Categoria any, Pageable any1);
}
