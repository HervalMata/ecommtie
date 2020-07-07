package com.herval.ecommtie.services;

import com.herval.ecommtie.model.entity.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ClienteService {
    Cliente save(Cliente any);

    Optional<Cliente> getById(Long id);

    void delete(Cliente cliente);

    Cliente update(Cliente cliente);

    Page<Cliente> find(Cliente filter, Pageable pageRequest);
}
