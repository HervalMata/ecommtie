package com.herval.ecommtie.services;

import com.herval.ecommtie.model.entity.Categoria;
import org.springframework.stereotype.Service;

@Service
public interface CategoriaService {
    Categoria save(Categoria categoria);
}
