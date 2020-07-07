package com.herval.ecommtie.repository;

import com.herval.ecommtie.model.entity.Categoria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class CategoriaRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    private CategoriaRepository repository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir uma categoria na base com o cpf informado")
    public void returnTrueWhenNomeExists() {
        String nome = "Categoria2";
        Categoria categoria = Categoria.builder().nome("Categoria2").build();
        entityManager.persist(categoria);
        boolean exists = repository.existsByNome(nome);
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar falso quando não existir uma categoria na base com o nome informado")
    public void returnFalseWhenCpfNotExists() {
        String nome = "Categoria2";
        boolean exists = repository.existsByNome(nome);
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve obter uma categoria por ID")
    public void findByIdTest() {
        Categoria categoria = createValidCategoria();
        entityManager.persist(categoria);
        Optional<Categoria> foundCategoria = repository.findById(categoria.getId());
        assertThat(foundCategoria.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Deve salvar uma categoria")
    public void saveCategoriaTest() {
        Categoria categoria = createValidCategoria();
        Categoria savedCategoria = repository.save(categoria);
        assertThat(savedCategoria.getId()).isNotNull();
    }

    @Test
    @DisplayName("Deve remover uma categoria")
    public void deleteCategoriaTest() {
        Categoria categoria = createValidCategoria();
        entityManager.persist(categoria);
        Categoria foundCategoria = entityManager.find(Categoria.class, categoria.getId());
        repository.delete(foundCategoria);
        Categoria deletedCategoria = entityManager.find(Categoria.class, categoria.getId());
        assertThat(deletedCategoria).isNull();
    }

    private Categoria createValidCategoria() {
        return Categoria.builder().nome("Categoria2").build();
    }
}
