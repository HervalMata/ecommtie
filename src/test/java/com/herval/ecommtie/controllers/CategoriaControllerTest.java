package com.herval.ecommtie.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.herval.ecommtie.dto.CategoriaDTO;
import com.herval.ecommtie.exceptions.NomeException;
import com.herval.ecommtie.model.entity.Categoria;
import com.herval.ecommtie.services.CategoriaService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = CategoriaController.class)
@AutoConfigureMockMvc
public class CategoriaControllerTest {

    static String CATEGORIA_API = "/api/categorias";

    @Autowired
    MockMvc mvc;

    @MockBean
    CategoriaService service;

    @Test
    @DisplayName("Deve criar uma categoria com sucesso!.")
    public void createCategoriaTest() throws Exception {
        CategoriaDTO dto = createNewCategoria();
        Categoria savedCategoria = Categoria.builder().id(1L).nome("Categoria1").build();
        BDDMockito.given(service.save(Mockito.any(Categoria.class))).willReturn(savedCategoria);
        String json = new ObjectMapper().writeValueAsString(dto);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(CATEGORIA_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("nome").value(dto.getNome()));
    }

    @Test
    @DisplayName("Deve lançar erro de validação quando não houver dados suficientes para criar uma categoria!.")
    public void createInvalidCategoriaTest() throws Exception {
        String json = new ObjectMapper().writeValueAsString(new CategoriaDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(CATEGORIA_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)));
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar cadastrar uma categoria com nome já utilizado")
    public void createClienteWithDuplicatedNome() throws Exception {
        CategoriaDTO dto = createNewCategoria();
        String json = new ObjectMapper().writeValueAsString(dto);
        String mensagemErro = "Nome já cadastrado";
        BDDMockito.given(service.save(Mockito.any(Categoria.class))).willThrow(new NomeException(mensagemErro));
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(CATEGORIA_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
        mvc
                .perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(mensagemErro));
    }

    @Test
    @DisplayName("Deve obter as informações de uma categoria!.")
    public void getCategoriaDetailsTest() throws Exception {
        Long id = 1L;
        Categoria categoria = Categoria.builder()
                .id(id)
                .nome(createNewCategoria().getNome())
                .build();
        BDDMockito.given(service.getById(id)).willReturn(Optional.of(categoria));
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(CATEGORIA_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);
        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("nome").value(createNewCategoria().getNome()));
    }

    @Test
    @DisplayName("Deve retornar not found quando a categoria procurada não existe!.")
    public void clienteNotFoundTest() throws Exception {
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(CATEGORIA_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);
        mvc
                .perform(request)
                .andExpect(status().isNotFound());
    }

    private CategoriaDTO createNewCategoria() {
        return CategoriaDTO.builder().nome("Categoria1").build();
    }
}
