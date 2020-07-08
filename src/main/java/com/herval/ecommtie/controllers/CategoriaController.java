package com.herval.ecommtie.controllers;

import com.herval.ecommtie.dto.CategoriaDTO;
import com.herval.ecommtie.exceptions.ApiErrors;
import com.herval.ecommtie.exceptions.NomeException;
import com.herval.ecommtie.model.entity.Categoria;
import com.herval.ecommtie.services.CategoriaService;
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
import java.util.List;
import java.util.stream.Collectors;

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

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Categoria categoria = service.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        service.delete(categoria);
    }

    @PutMapping("{id}")
    public CategoriaDTO update(@PathVariable Long id, CategoriaDTO dto) {
        return service.getById(id).map( categoria -> {
            categoria.setNome(dto.getNome());
            categoria = service.update(categoria);
            return mapper.map(categoria, CategoriaDTO.class);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public Page<CategoriaDTO> find(CategoriaDTO dto, Pageable pageRequest) {
        Categoria filter = mapper.map(dto, Categoria.class);
        Page<Categoria> result = service.find(filter, pageRequest);
        List<CategoriaDTO> list = result.getContent()
                .stream()
                .map(entity -> mapper.map(entity, CategoriaDTO.class))
                .collect(Collectors.toList());
        return new PageImpl<CategoriaDTO>(list, pageRequest, result.getTotalElements());
    }
}
