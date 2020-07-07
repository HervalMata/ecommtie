package com.herval.ecommtie.services;

import com.herval.ecommtie.exceptions.NomeException;
import com.herval.ecommtie.model.entity.Categoria;
import com.herval.ecommtie.repository.CategoriaRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public class CategoriaServiceImpl implements CategoriaService {

    private CategoriaRepository repository;

    public CategoriaServiceImpl(CategoriaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Categoria save(Categoria categoria) {
        if (repository.existsByNome(categoria.getNome())) {
            throw new NomeException("Nome já cadastrado");
        }
        return repository.save(categoria);
    }

    @Override
    public Optional<Categoria> getById(Long id) {
        return this.repository.findById(id);
    }

    @Override
    public void delete(Categoria categoria) {
        if (categoria == null || categoria.getId() == null) {
            throw new IllegalArgumentException("Categoria não pode ser nula");
        }
        this.repository.delete(categoria);
    }

    @Override
    public Categoria update(Categoria categoria) {
        if (categoria == null || categoria.getId() == null) {
            throw new IllegalArgumentException("Categoria não pode ser nula");
        }
        return this.repository.save(categoria);
    }

    @Override
    public Page<Categoria> find(Categoria filter, Pageable pageRequest) {
        Example<Categoria> example = Example.of(filter,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withIncludeNullValues()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return repository.findAll(example, pageRequest);
    }
}
