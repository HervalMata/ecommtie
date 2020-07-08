package com.herval.ecommtie.services;

import com.herval.ecommtie.exceptions.NomeException;
import com.herval.ecommtie.model.entity.Produto;
import com.herval.ecommtie.repository.ProdutoRepository;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class ProdutoServiceTest {

    @MockBean
    ProdutoRepository repository;
    ProdutoService service;

    @BeforeEach
    public void setUp() {
        this.service = new ProdutoServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um produto")
    public void saveProdutoTest() {

        Produto produto = createValidProduto();
        Mockito.when(repository.save(produto)).thenReturn(
                Produto.builder()
                        .id(2L).nome("Produto2").build());
        Produto savedProduto = service.save(produto);
        assertThat(savedProduto.getId()).isNotNull();
        assertThat(savedProduto.getNome()).isEqualTo("Produto2");
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar cadastrar um produto com nome já utilizado")
    public void createProdutoWithDuplicatedNome() {
        Produto produto = createValidProduto();
        Mockito.when(repository.existsByNome(Mockito.anyString())).thenReturn(true);
        Throwable exception = Assertions.catchThrowable(() -> service.save(produto));
        assertThat(exception)
                .isInstanceOf(NomeException.class)
                .hasMessage("Nome já cadastrado");
        Mockito.verify(repository, Mockito.never()).save(produto);
    }

    @Test
    @DisplayName("Deve obter um cliente por ID")
    public void getByIdProdutoTest() {
        Long id = 1L;
        Produto produto = createValidProduto();
        produto.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(produto));
        Optional<Produto> foundProduto = service.getById(id);
        assertThat(foundProduto.isPresent()).isTrue();
        assertThat(foundProduto.get().getId()).isEqualTo(id);
        assertThat(foundProduto.get().getNome()).isEqualTo(produto.getNome());
    }

    @Test
    @DisplayName("Deve retornar em branco ao procurar um produto que não existe")
    public void notFoundByIdProdutoTest() {
        Long id = 1L;
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
        Optional<Produto> foundProduto = service.getById(id);
        assertThat(foundProduto.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Deve remover um produto")
    public void deleteProdutoTest() {
        Produto produto = Produto.builder().id(1L).build();
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.delete(produto));
        Mockito.verify(repository, Mockito.times(1)).delete(produto);
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar remover um produto inexistente")
    public void deleteInexistentProdutoTest() {
        Produto produto = new Produto();
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(produto));
        Mockito.verify(repository, Mockito.never()).delete(produto);
    }

    @Test
    @DisplayName("Deve atualizar um produto")
    public void updateProdutoTest() {
        Long id = 1L;
        Produto updatingProduto = Produto.builder().id(1L).build();
        Produto updatedProduto = createValidProduto();
        updatedProduto.setId(id);
        Mockito.when(repository.save(updatingProduto)).thenReturn(updatedProduto);
        Produto produto = service.update(updatingProduto);
        assertThat(produto.getId()).isEqualTo(updatedProduto.getId());
        assertThat(produto.getNome()).isEqualTo(updatedProduto.getNome());
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar atualizar um produto inexistente")
    public void updateInexistentProdutoTest() {
        Produto produto = new Produto();
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(produto));
        Mockito.verify(repository, Mockito.never()).save(produto);
    }

    @Test
    @DisplayName("Deve filtrar um produto pelo nome")
    public void findProdutoTest() {
        Produto produto = createValidProduto();
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Produto> lista = Arrays.asList(produto);
        Page<Produto> page = new PageImpl<Produto>(lista, pageRequest, 1);
        Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class))).thenReturn(page);
        Page<Produto> result = service.find(produto, pageRequest);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    private Produto createValidProduto() {
        return Produto.builder().nome("Produto2").build();
    }
}
