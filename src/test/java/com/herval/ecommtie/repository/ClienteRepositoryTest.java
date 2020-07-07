package com.herval.ecommtie.repository;

import com.herval.ecommtie.model.entity.Cliente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class ClienteRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    ClienteRepository repository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um cliente na base com o cpf informado")
    public void returnTrueWhenCpfExists() {
        String cpf = "11122233344";
        Cliente cliente = Cliente.builder().nome("Cliente2").cpf("11122233344").dataCadastro(LocalDate.now()).build();
        entityManager.persist(cliente);
        boolean exists = repository.existsByCpf(cpf);
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar falso quando não existir um cliente na base com o cpf informado")
    public void returnFalseWhenCpfNotExists() {
        String cpf = "11122233344";
        boolean exists = repository.existsByCpf(cpf);
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve obter um cliente por ID")
    public void findByIdTest() {
        Cliente cliente = createValidCliente();
        entityManager.persist(cliente);
        Optional<Cliente> foundCliente = repository.findById(cliente.getId());
        assertThat(foundCliente.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Deve salvar um cliente")
    public void saveClienteTest() {
        Cliente cliente = createValidCliente();
        Cliente savedCliente = repository.save(cliente);
        assertThat(savedCliente.getId()).isNotNull();
    }

    @Test
    @DisplayName("Deve remover um cliente")
    public void deleteClienteTest() {
        Cliente cliente = createValidCliente();
        entityManager.persist(cliente);
        Cliente foundCliente = entityManager.find(Cliente.class, cliente.getId());
        repository.delete(foundCliente);
        Cliente deletedCliente = entityManager.find(Cliente.class, cliente.getId());
        assertThat(deletedCliente).isNull();
    }

    private Cliente createValidCliente() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataCadastro = LocalDate.parse("05/05/2020", formato);

        return Cliente.builder().nome("Cliente2").cpf("99988877766").dataCadastro(dataCadastro).build();
    }
}
