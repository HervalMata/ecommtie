package com.herval.ecommtie.services;

import com.herval.ecommtie.model.entity.Categoria;
import com.herval.ecommtie.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class CategoriaServiceTest {

    @MockBean
    CategoriaRepository repository;
    CategoriaService service;

    @BeforeEach
    public void setUp() {
        this.service = new CategoriaServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar uma categoria")
    public void saveCategoriaTest() {

        Categoria categoria = createValidCategoria();
        Mockito.when(repository.save(categoria)).thenReturn(
                Categoria.builder()
                        .id(2L).nome("Categoria2").build());
        Categoria savedCategoria = service.save(categoria);
        assertThat(savedCategoria.getId()).isNotNull();
        assertThat(savedCategoria.getNome()).isEqualTo("Categoria2");
    }

    private Categoria createValidCategoria() {
        return Categoria.builder().nome("Categoria2").build();
    }
}
