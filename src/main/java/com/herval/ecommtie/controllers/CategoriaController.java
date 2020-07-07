package com.herval.ecommtie.controllers;

import com.herval.ecommtie.dto.CategoriaDTO;
import com.herval.ecommtie.model.entity.Categoria;
import com.herval.ecommtie.services.CategoriaService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
}
