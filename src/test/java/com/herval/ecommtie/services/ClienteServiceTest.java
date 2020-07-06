package com.herval.ecommtie.services;

import com.herval.ecommtie.exceptions.CpfException;
import com.herval.ecommtie.model.entity.Cliente;
import com.herval.ecommtie.repository.ClienteRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class ClienteServiceTest {

    @MockBean
    ClienteRepository repository;
    ClienteService service;

    @BeforeEach
    public void setUp() {
        this.service = new ClienteServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um cliente")
    public void saveClienteTest() {

        Cliente cliente = createValidCliente();
        Mockito.when(repository.save(cliente)).thenReturn(
                Cliente.builder()
                .id(2L).nome("Cliente2")
                .cpf("99988877766")
                .dataCadastro(cliente.getDataCadastro()).build()
        );
        Cliente savedCliente = service.save(cliente);
        assertThat(savedCliente.getId()).isNotNull();
        assertThat(savedCliente.getNome()).isEqualTo("Cliente2");
        assertThat(savedCliente.getCpf()).isEqualTo("99988877766");
        assertThat(savedCliente.getDataCadastro()).isEqualTo(cliente.getDataCadastro());
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar cadastrar um cliente com cpf já utilizado")
    public void createClienteWithDuplicatedCpf() {
        Cliente cliente = createValidCliente();
        Mockito.when(repository.existsByCpf(Mockito.anyString())).thenReturn(true);
        Throwable exception = Assertions.catchThrowable(() -> service.save(cliente));
        assertThat(exception)
                .isInstanceOf(CpfException.class)
                .hasMessage("Cpf já cadastrado");
        Mockito.verify(repository, Mockito.never()).save(cliente);
    }

    private Cliente createValidCliente() {DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataCadastro = LocalDate.parse("05/05/2020", formato);

        return Cliente.builder().nome("Cliente2").cpf("99988877766").dataCadastro(dataCadastro).build();
    }
}
