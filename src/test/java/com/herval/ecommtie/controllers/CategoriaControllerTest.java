package com.herval.ecommtie.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.herval.ecommtie.dto.CategoriaDTO;
import com.herval.ecommtie.model.entity.Categoria;
import com.herval.ecommtie.services.CategoriaService;
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

    private CategoriaDTO createNewCategoria() {
        return CategoriaDTO.builder().nome("Categoria1").build();
    }
}
