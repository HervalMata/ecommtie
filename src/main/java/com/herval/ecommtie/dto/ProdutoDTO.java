package com.herval.ecommtie.dto;

import com.herval.ecommtie.model.entity.Categoria;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoDTO {

    private Long id;
    @NotEmpty
    private String nome;
    @NotEmpty
    private String cor;
    @NotEmpty
    private String material;
    @NotNull
    private int estoque;
    @NotNull
    private double preco;
    @NotNull
    private Categoria categoria;

}
