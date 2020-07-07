package com.herval.ecommtie.services;

import com.herval.ecommtie.exceptions.CpfException;
import com.herval.ecommtie.model.entity.Cliente;
import com.herval.ecommtie.repository.ClienteRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    private ClienteRepository repository;

    public ClienteServiceImpl(ClienteRepository repository) {
        this.repository = repository;
    }


    @Override
    public Cliente save(Cliente cliente) {
        if (repository.existsByCpf(cliente.getCpf())) {
            throw new CpfException("Cpf já cadastrado");
        }
        return repository.save(cliente);
    }

    @Override
    public Optional<Cliente> getById(Long id) {
        return this.repository.findById(id);
    }

    @Override
    public void delete(Cliente cliente) {
        if (cliente == null || cliente.getId() == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo");
        }
        this.repository.delete(cliente);
    }

    @Override
    public Cliente update(Cliente cliente) {
        if (cliente == null || cliente.getId() == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo");
        }
        return this.repository.save(cliente);
    }

    @Override
    public Page<Cliente> find(Cliente filter, Pageable pageRequest) {
        Example<Cliente> example = Example.of(filter,
                ExampleMatcher.matching()
                .withIgnoreCase()
                .withIncludeNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return repository.findAll(example, pageRequest);
    }
}
