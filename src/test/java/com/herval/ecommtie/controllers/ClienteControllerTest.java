package com.herval.ecommtie.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.herval.ecommtie.dto.ClienteDTO;
import com.herval.ecommtie.exceptions.CpfException;
import com.herval.ecommtie.model.entity.Cliente;
import com.herval.ecommtie.services.ClienteService;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = ClienteController.class)
@AutoConfigureMockMvc
public class ClienteControllerTest {

    static String CLIENTE_API = "/api/clientes";

    @Autowired
    MockMvc mvc;

    @MockBean
    ClienteService service;

    @Test
    @DisplayName("Deve criar um cliente com sucesso!.")
    public void createClienteTest() throws Exception {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate data = LocalDate.parse("05/05/2020", formato);
        ClienteDTO dto = createNewCliente();
        Cliente savedCliente = Cliente.builder().id(1L).nome("Cliente1").cpf("11122233344").dataCadastro(LocalDate.now()).build();
        BDDMockito.given(service.save(Mockito.any(Cliente.class))).willReturn(savedCliente);
        String json = new ObjectMapper().writeValueAsString(dto);
                MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                        .post(CLIENTE_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json);

        mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("nome").value(dto.getNome()))
                .andExpect(jsonPath("cpf").value(dto.getCpf()))
                .andExpect(jsonPath("dataCadastro").value(dto.getDataCadastro()));
    }

    @Test
    @DisplayName("Deve lançar erro de validação quando não houver dados suficientes para criar um cliente!.")
    public void createInvalidClienteTest() throws Exception {
        String json = new ObjectMapper().writeValueAsString(new ClienteDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(CLIENTE_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(3)));
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar cadastrar um cliente com cpf já utilizado")
    public void createClienteWithDuplicatedCpf() throws Exception {
        ClienteDTO dto = createNewCliente();
        String json = new ObjectMapper().writeValueAsString(dto);
        String mensagemErro = "Cpf já cadastrado";
        BDDMockito.given(service.save(Mockito.any(Cliente.class))).willThrow(new CpfException(mensagemErro));
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(CLIENTE_API)
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
    @DisplayName("Deve obter as informações de um cliente!.")
    public void getClienteDetailsTest() throws Exception {
        Long id = 1L;
        Cliente cliente = Cliente.builder()
                .id(id)
                .nome(createNewCliente().getNome())
                .cpf(createNewCliente().getCpf())
                .dataCadastro(createNewCliente().getDataCadastro())
                .build();
        BDDMockito.given(service.getById(id)).willReturn(Optional.of(cliente));
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(CLIENTE_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);
        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("nome").value(createNewCliente().getNome()))
                .andExpect(jsonPath("cpf").value(createNewCliente().getCpf()));
    }

    @Test
    @DisplayName("Deve retornar not found quando o cliente procurado não existe!.")
    public void clienteNotFoundTest() throws Exception {
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(CLIENTE_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);
        mvc
                .perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve remover um cliente.")
    public void deleteClienteTest() throws Exception {
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.of(Cliente.builder().id(1L).build()));
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(CLIENTE_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);
        mvc
                .perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar not found quando não encontrar o cliente para remover!.")
    public void deleteClienteNotFoundTest() throws Exception {
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(CLIENTE_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);
        mvc
                .perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar um cliente.")
    public void updateClienteTest() throws Exception {
        Long id = 1L;
        String json = new ObjectMapper().writeValueAsString(createNewCliente());
        Cliente updatingCliente = Cliente.builder()
                .id(id)
                .nome("Cliente2")
                .cpf("11122233344")
                .dataCadastro(createNewCliente().getDataCadastro())
                .build();
        BDDMockito.given(service.getById(id)).willReturn(Optional.of(updatingCliente));
        Cliente updatedCliente = Cliente.builder()
                .id(id)
                .nome("Cliente1")
                .cpf("11122233344")
                .dataCadastro(createNewCliente().getDataCadastro())
                .build();
        BDDMockito.given(service.update(updatingCliente)).willReturn(updatedCliente);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(CLIENTE_API.concat("/" + 1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("nome").value(createNewCliente().getNome()))
                .andExpect(jsonPath("cpf").value(createNewCliente().getCpf()));
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar atualizar um cliente inexistente.")
    public void updateClienteNotFoundTest() throws Exception {
        String json = new ObjectMapper().writeValueAsString(createNewCliente());
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(CLIENTE_API.concat("/" + 1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
        mvc
                .perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve filtrar clientes.")
    public void findClientesTest() throws Exception {
        Long id = 1L;
        Cliente cliente = Cliente.builder()
                .id(id)
                .nome(createNewCliente().getNome())
                .cpf(createNewCliente().getCpf())
                .dataCadastro(createNewCliente().getDataCadastro())
                .build();
        BDDMockito.given(service.find(Mockito.any(Cliente.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Cliente>(Arrays.asList(cliente), PageRequest.of(0, 100), 1));
        String queryString = String.format("?nome=%s&cpf=%s&page=0&size=100", cliente.getNome(), cliente.getCpf());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(CLIENTE_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);
        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }

    private ClienteDTO createNewCliente() {
        return ClienteDTO.builder().nome("Cliente1").cpf("11122233344").dataCadastro(LocalDate.now()).build();
    }
}
