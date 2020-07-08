package com.herval.ecommtie.controllers;

import com.herval.ecommtie.dto.ClienteDTO;
import com.herval.ecommtie.exceptions.ApiErrors;
import com.herval.ecommtie.exceptions.CpfException;
import com.herval.ecommtie.model.entity.Cliente;
import com.herval.ecommtie.services.ClienteService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService service;

    private final ModelMapper mapper;

    public ClienteController(ClienteService service, ModelMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteDTO create(@RequestBody @Valid ClienteDTO dto) {
        Cliente entity = Cliente.builder()
                    .nome(dto.getNome())
                    .cpf(dto.getCpf())
                    .dataCadastro(LocalDate.now()).build();

        entity = service.save(entity);

        return mapper.map(entity, ClienteDTO.class);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Cliente cliente = service.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        service.delete(cliente);
    }

    @PutMapping("{id}")
    public ClienteDTO update(@PathVariable Long id, ClienteDTO dto) {
        return service.getById(id).map( cliente -> {
            cliente.setNome(dto.getNome());
            cliente = service.update(cliente);
            return mapper.map(cliente, ClienteDTO.class);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("{id}")
    public ClienteDTO get(@PathVariable Long id) {
        return service
                .getById(id)
                .map(cliente -> mapper.map(cliente, ClienteDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public Page<ClienteDTO> find(ClienteDTO dto, Pageable pageRequest) {
        Cliente filter = mapper.map(dto, Cliente.class);
        Page<Cliente> result = service.find(filter, pageRequest);
        List<ClienteDTO> list = result.getContent()
                .stream()
                .map(entity -> mapper.map(entity, ClienteDTO.class))
                .collect(Collectors.toList());
        return new PageImpl<ClienteDTO>(list, pageRequest, result.getTotalElements());
    }
}
