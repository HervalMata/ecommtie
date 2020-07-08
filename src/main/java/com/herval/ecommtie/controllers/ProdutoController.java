package com.herval.ecommtie.controllers;

import com.herval.ecommtie.dto.ProdutoDTO;
import com.herval.ecommtie.model.entity.Categoria;
import com.herval.ecommtie.model.entity.Produto;
import com.herval.ecommtie.services.CategoriaService;
import com.herval.ecommtie.services.ProdutoService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private ProdutoService produtoService;
    private CategoriaService categoriaService;
    private ModelMapper mapper;

    public ProdutoController(ProdutoService produtoService, CategoriaService categoriaService, ModelMapper mapper) {
        this.produtoService = produtoService;
        this.categoriaService = categoriaService;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProdutoDTO create(@RequestBody @Valid ProdutoDTO dto) {
        Categoria categoria = categoriaService.getById(dto.getCategoria().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoria não pode ser nula."));
        Produto produto = Produto.builder()
                .nome(dto.getNome())
                .cor(dto.getCor())
                .material(dto.getMaterial())
                .estoque(dto.getEstoque())
                .preco(dto.getPreco())
                .categoria(categoria).build();
        produto = produtoService.save(produto);
        return mapper.map(produto, ProdutoDTO.class);
    }

    @GetMapping("{id}")
    public ProdutoDTO get(@PathVariable Long id) {
        return produtoService
                .getById(id)
                .map(produto -> mapper.map(produto, ProdutoDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
