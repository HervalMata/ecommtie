package com.herval.ecommtie.controllers;

import com.herval.ecommtie.dto.CategoriaDTO;
import com.herval.ecommtie.exceptions.ApiErrors;
import com.herval.ecommtie.exceptions.NomeException;
import com.herval.ecommtie.model.entity.Categoria;
import com.herval.ecommtie.services.CategoriaService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private CategoriaService service;
    private ModelMapper mapper;

    public CategoriaController(CategoriaService service, ModelMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoriaDTO create(@RequestBody @Valid CategoriaDTO dto) {
        Categoria categoria = mapper.map(dto, Categoria.class);

        categoria = service.save(categoria);

        return mapper.map(categoria, CategoriaDTO.class);
    }

    @GetMapping("{id}")
    public CategoriaDTO get(@PathVariable Long id) {
        return service
                .getById(id)
                .map(categoria -> mapper.map(categoria, CategoriaDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        return new ApiErrors(bindingResult);
    }

    @ExceptionHandler(NomeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleNomeException(NomeException ex) {
        return new ApiErrors(ex);
    }
}
