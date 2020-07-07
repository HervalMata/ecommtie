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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    @Test
    @DisplayName("Deve obter um cliente por ID")
    public void getByIdClienteTest() {
        Long id = 1L;
        Cliente cliente = createValidCliente();
        cliente.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(cliente));
        Optional<Cliente> foundCliente = service.getById(id);
        assertThat(foundCliente.isPresent()).isTrue();
        assertThat(foundCliente.get().getId()).isEqualTo(id);
        assertThat(foundCliente.get().getNome()).isEqualTo(cliente.getNome());
        assertThat(foundCliente.get().getCpf()).isEqualTo(cliente.getCpf());
        assertThat(foundCliente.get().getDataCadastro()).isEqualTo(cliente.getDataCadastro());
    }

    @Test
    @DisplayName("Deve retornar em branco ao procurar um cliente que não existe")
    public void notFoundByIdClienteTest() {
        Long id = 1L;
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
        Optional<Cliente> foundCliente = service.getById(id);
        assertThat(foundCliente.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Deve remover um cliente")
    public void deleteClienteTest() {
        Cliente cliente = Cliente.builder().id(1L).build();
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.delete(cliente));
        Mockito.verify(repository, Mockito.times(1)).delete(cliente);
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar remover um cliente inexistente")
    public void deleteInexistentClienteTest() {
        Cliente cliente = new Cliente();
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(cliente));
        Mockito.verify(repository, Mockito.never()).delete(cliente);
    }

    @Test
    @DisplayName("Deve atualizar um cliente")
    public void updateClienteTest() {
        Long id = 1L;
        Cliente updatingCliente = Cliente.builder().id(1L).build();
        Cliente updatedCliente = createValidCliente();
        updatedCliente.setId(id);
        Mockito.when(repository.save(updatingCliente)).thenReturn(updatedCliente);
        Cliente cliente = service.update(updatingCliente);
        assertThat(cliente.getId()).isEqualTo(updatedCliente.getId());
        assertThat(cliente.getNome()).isEqualTo(updatedCliente.getNome());
        assertThat(cliente.getCpf()).isEqualTo(updatedCliente.getCpf());
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar atualizar um cliente inexistente")
    public void updateInexistentClienteTest() {
        Cliente cliente = new Cliente();
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(cliente));
        Mockito.verify(repository, Mockito.never()).save(cliente);
    }

    @Test
    @DisplayName("Deve filtrar um cliente pelo nome ou cpf")
    public void findClienteTest() {
        Cliente cliente = createValidCliente();
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Cliente> lista = Arrays.asList(cliente);
        Page<Cliente> page = new PageImpl<Cliente>(lista, pageRequest, 1);
        Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class))).thenReturn(page);
        Page<Cliente> result = service.find(cliente, pageRequest);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    private Cliente createValidCliente() {DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataCadastro = LocalDate.parse("05/05/2020", formato);

        return Cliente.builder().nome("Cliente2").cpf("99988877766").dataCadastro(dataCadastro).build();
    }
}
