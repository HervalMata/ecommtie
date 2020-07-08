package com.herval.ecommtie.services;

import com.herval.ecommtie.exceptions.NomeException;
import com.herval.ecommtie.model.entity.Produto;
import com.herval.ecommtie.repository.ProdutoRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public class ProdutoServiceImpl implements ProdutoService {

    private ProdutoRepository repository;

    public ProdutoServiceImpl(ProdutoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Produto save(Produto produto) {
        if (repository.existsByNome(produto.getNome())) {
            throw new NomeException("Nome já cadastrado");
        }
        return repository.save(produto);
    }

    @Override
    public Optional<Produto> getById(Long id) {
        return this.repository.findById(id);
    }

    @Override
    public void delete(Produto produto) {
        if (produto == null || produto.getId() == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo.");
        }
        this.repository.delete(produto);
    }

    @Override
    public Produto update(Produto produto) {
        if (produto == null || produto.getId() == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo.");
        }
        return this.repository.save(produto);
    }

    @Override
    public Page<Produto> find(Produto filter, Pageable pageRequest) {
        Example<Produto> example = Example.of(filter,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withIncludeNullValues()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return repository.findAll(example, pageRequest);
    }
}
