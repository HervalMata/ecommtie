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
}
