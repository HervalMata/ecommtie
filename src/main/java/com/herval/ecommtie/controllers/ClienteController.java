package com.herval.ecommtie.controllers;

import com.herval.ecommtie.dto.ClienteDTO;
import com.herval.ecommtie.exceptions.ApiErrors;
import com.herval.ecommtie.exceptions.CpfException;
import com.herval.ecommtie.model.entity.Cliente;
import com.herval.ecommtie.services.ClienteService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
        Cliente entity = mapper.map(dto, Cliente.class);

        entity = service.save(entity);

        return mapper.map(entity, ClienteDTO.class);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        return new ApiErrors(bindingResult);
    }

    @ExceptionHandler(CpfException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleCpfException(CpfException ex) {
        return new ApiErrors(ex);
    }
}
