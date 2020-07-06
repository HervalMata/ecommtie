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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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


    private ClienteDTO createNewCliente() {
        return ClienteDTO.builder().nome("Cliente1").cpf("11122233344").dataCadastro(LocalDate.now()).build();
    }
}
