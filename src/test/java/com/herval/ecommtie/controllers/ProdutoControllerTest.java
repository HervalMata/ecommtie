package com.herval.ecommtie.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.herval.ecommtie.dto.ProdutoDTO;
import com.herval.ecommtie.exceptions.NomeException;
import com.herval.ecommtie.model.entity.Categoria;
import com.herval.ecommtie.model.entity.Produto;
import com.herval.ecommtie.services.CategoriaService;
import com.herval.ecommtie.services.ProdutoService;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = ProdutoController.class)
@AutoConfigureMockMvc
public class ProdutoControllerTest {

    static String PRODUTO_API  = "/api/produtos";

    @Autowired
    MockMvc mvc;

    @MockBean
    private CategoriaService categoriaService;

    @MockBean
    private ProdutoService produtoService;

    @Test
    @DisplayName("Deve cadastrar um produto")
    public void createProdutoTest() throws Exception {
        ProdutoDTO dto = createProdutoNovoTest();
        String json = new ObjectMapper().writeValueAsString(dto);
        Categoria categoria = Categoria.builder()
                .id(1L).nome("Categoria1").build();
        BDDMockito.given(categoriaService.getById(categoria.getId())).willReturn(Optional.of(categoria));
        Produto produto = Produto.builder()
                .id(1L).nome(dto.getNome())
                .cor(dto.getCor())
                .material(dto.getMaterial())
                .estoque(dto.getEstoque())
                .preco(dto.getPreco())
                .categoria(categoria).build();
        BDDMockito.given(produtoService.save(Mockito.any(Produto.class))).willReturn(produto);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(PRODUTO_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("nome").value(dto.getNome()))
                .andExpect(jsonPath("cor").value(dto.getCor()));

    }

    @Test
    @DisplayName("Deve retornar erro ao tentar cadastrar um produto inexistente")
    public void createInvalidProdutoTest() throws Exception {
        ProdutoDTO dto = createProdutoNovoTest();
        String json = new ObjectMapper().writeValueAsString(dto);
        BDDMockito.given(categoriaService.getById(Mockito.anyLong())).willReturn(Optional.empty());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(PRODUTO_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
        mvc
                .perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Categoria não pode ser nula."));
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar cadastrar um produto com nome já utilizado")
    public void createProdutoWithDuplicatedNome() throws Exception {
        ProdutoDTO dto = createProdutoNovoTest();
        String json = new ObjectMapper().writeValueAsString(dto);
        Categoria categoria = Categoria.builder()
                .id(1L).nome("Categoria1").build();
        BDDMockito.given(categoriaService.getById(categoria.getId())).willReturn(Optional.of(categoria));
        String mensagemErro = "Nome já cadastrado";
        BDDMockito.given(produtoService.save(Mockito.any(Produto.class))).willThrow(new NomeException(mensagemErro));
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(PRODUTO_API)
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
    @DisplayName("Deve obter as informações de um produto!.")
    public void getProdutoDetailsTest() throws Exception {
        Long id = 1L;
        Produto produto = Produto.builder()
                .id(id)
                .nome(createProdutoNovoTest().getNome())
                .cor(createProdutoNovoTest().getCor())
                .build();
        BDDMockito.given(produtoService.getById(id)).willReturn(Optional.of(produto));
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(PRODUTO_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);
        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("nome").value(createProdutoNovoTest().getNome()))
                .andExpect(jsonPath("cor").value(createProdutoNovoTest().getCor()));
    }

    @Test
    @DisplayName("Deve retornar not found quando o produto procurado não existe!.")
    public void produtoNotFoundTest() throws Exception {
        BDDMockito.given(produtoService.getById(Mockito.anyLong())).willReturn(Optional.empty());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(PRODUTO_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);
        mvc
                .perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve remover um produto.")
    public void deleteProdutoTest() throws Exception {
        BDDMockito.given(produtoService.getById(Mockito.anyLong())).willReturn(Optional.of(Produto.builder().id(1L).build()));
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(PRODUTO_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);
        mvc
                .perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar not found quando não encontrar o produto para remover!.")
    public void deleteProdutoNotFoundTest() throws Exception {
        BDDMockito.given(produtoService.getById(Mockito.anyLong())).willReturn(Optional.empty());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(PRODUTO_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);
        mvc
                .perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar um produto.")
    public void updateProdutoTest() throws Exception {
        Long id = 1L;
        String json = new ObjectMapper().writeValueAsString(createProdutoNovoTest());
        Produto updatingProduto = Produto.builder()
                .id(id)
                .nome("Produto2")
                .cor("Azul")
                .material("Lonita")
                .estoque(1)
                .preco(25.00)
                .build();
        BDDMockito.given(produtoService.getById(id)).willReturn(Optional.of(updatingProduto));
        Produto updatedProduto = Produto.builder()
                .id(id)
                .nome("Produto1")
                .cor("Rosa")
                .material("Chita")
                .preco(2)
                .preco(30.00)
                .build();
        BDDMockito.given(produtoService.update(updatingProduto)).willReturn(updatedProduto);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(PRODUTO_API.concat("/" + 1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("nome").value(updatedProduto.getNome()))
                .andExpect(jsonPath("cor").value(updatedProduto.getCor()));
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar atualizar um produto inexistente.")
    public void updateProdutoNotFoundTest() throws Exception {
        String json = new ObjectMapper().writeValueAsString(createProdutoNovoTest());
        BDDMockito.given(produtoService.getById(Mockito.anyLong())).willReturn(Optional.empty());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(PRODUTO_API.concat("/" + 1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
        mvc
                .perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve filtrar produtos.")
    public void findProdutosTest() throws Exception {
        Long id = 1L;
        Produto produto = Produto.builder()
                .id(id)
                .nome(createProdutoNovoTest().getNome())
                .cor(createProdutoNovoTest().getCor())
                .material(createProdutoNovoTest().getMaterial())
                .build();
        BDDMockito.given(produtoService.find(Mockito.any(Produto.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Produto>(Arrays.asList(produto), PageRequest.of(0, 100), 1));
        String queryString = String.format("?nome=%s&cor=%s&material=%s&page=0&size=100", produto.getNome(), produto.getCor(), produto.getMaterial());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(PRODUTO_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);
        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }

    private ProdutoDTO createProdutoNovoTest() {
        Long id = 1L;
        return ProdutoDTO.builder().nome("Produto1").cor("Azul").material("Lonita").estoque(1).preco(25.00).categoria(Categoria.builder().id(id).build()).build();
    }
}
