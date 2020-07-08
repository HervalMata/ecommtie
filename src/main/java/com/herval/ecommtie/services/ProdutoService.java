package com.herval.ecommtie.services;

import com.herval.ecommtie.model.entity.Produto;
import org.springframework.stereotype.Service;

@Service
public interface ProdutoService {
    Produto save(Produto produto);
}
