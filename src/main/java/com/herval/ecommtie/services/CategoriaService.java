package com.herval.ecommtie.services;

import com.herval.ecommtie.model.entity.Categoria;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface CategoriaService {
    Categoria save(Categoria categoria);

    Optional<Categoria> getById(Long id);

    void delete(Categoria categoria);
}
