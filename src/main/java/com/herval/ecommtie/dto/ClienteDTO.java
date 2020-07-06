package com.herval.ecommtie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {

    private Long id;
    @NotEmpty
    private String nome;
    @NotEmpty
    private String cpf;
    //@JsonFormat(pattern = "dd/MM/yyyy")
    @NotNull
    private LocalDate dataCadastro;
}
