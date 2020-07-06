package com.herval.ecommtie.services;

import com.herval.ecommtie.exceptions.CpfException;
import com.herval.ecommtie.model.entity.Cliente;
import com.herval.ecommtie.repository.ClienteRepository;
import org.springframework.stereotype.Service;

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
}
