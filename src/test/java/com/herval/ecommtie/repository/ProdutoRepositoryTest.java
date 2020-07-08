package com.herval.ecommtie.repository;

import com.herval.ecommtie.model.entity.Produto;
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
public class ProdutoRepositoryTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    private ProdutoRepository repository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um produto na base com o cpf informado")
    public void returnTrueWhenNomeExists() {
        String nome = "Produto2";
        Produto produto = Produto.builder().nome("Produto2").build();
        entityManager.persist(produto);
        boolean exists = repository.existsByNome(nome);
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar falso quando não existir um produto na base com o nome informado")
    public void returnFalseWhenNomeNotExists() {
        String nome = "Produto2";
        boolean exists = repository.existsByNome(nome);
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve obter um produto por ID")
    public void findByIdTest() {
        Produto produto = createValidProduto();
        entityManager.persist(produto);
        Optional<Produto> foundProduto = repository.findById(produto.getId());
        assertThat(foundProduto.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Deve salvar um produto")
    public void saveProdutoTest() {
        Produto produto = createValidProduto();
        Produto savedProduto = repository.save(produto);
        assertThat(savedProduto.getId()).isNotNull();
    }

    @Test
    @DisplayName("Deve remover um produto")
    public void deleteProdutoTest() {
        Produto produto = createValidProduto();
        entityManager.persist(produto);
        Produto foundProduto = entityManager.find(Produto.class, produto.getId());
        repository.delete(foundProduto);
        Produto deletedProduto = entityManager.find(Produto.class, produto.getId());
        assertThat(deletedProduto).isNull();
    }

    private Produto createValidProduto() {
        return Produto.builder().nome("Produto2").build();
    }
}
