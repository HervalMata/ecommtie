package com.herval.ecommtie.services;

import com.herval.ecommtie.model.entity.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface ProdutoService {
    Produto save(Produto produto);

    Optional<Produto> getById(Long id);

    void delete(Produto produto);

    Produto update(Produto produto);

    Page<Produto> find(Produto filter, Pageable pageRequest);
}
