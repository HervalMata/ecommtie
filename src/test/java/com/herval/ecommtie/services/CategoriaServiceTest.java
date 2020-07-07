package com.herval.ecommtie.services;

import com.herval.ecommtie.exceptions.NomeException;
import com.herval.ecommtie.model.entity.Categoria;
import com.herval.ecommtie.repository.CategoriaRepository;
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
public class CategoriaServiceTest {

    @MockBean
    CategoriaRepository repository;
    CategoriaService service;

    @BeforeEach
    public void setUp() {
        this.service = new CategoriaServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar uma categoria")
    public void saveCategoriaTest() {

        Categoria categoria = createValidCategoria();
        Mockito.when(repository.save(categoria)).thenReturn(
                Categoria.builder()
                        .id(2L).nome("Categoria2").build());
        Categoria savedCategoria = service.save(categoria);
        assertThat(savedCategoria.getId()).isNotNull();
        assertThat(savedCategoria.getNome()).isEqualTo("Categoria2");
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar cadastrar uma categoria com nome já utilizado")
    public void createCategoriaWithDuplicatedNome() {
        Categoria categoria = createValidCategoria();
        Mockito.when(repository.existsByNome(Mockito.anyString())).thenReturn(true);
        Throwable exception = Assertions.catchThrowable(() -> service.save(categoria));
        assertThat(exception)
                .isInstanceOf(NomeException.class)
                .hasMessage("Nome já cadastrado");
        Mockito.verify(repository, Mockito.never()).save(categoria);
    }

    @Test
    @DisplayName("Deve obter uma cliente por ID")
    public void getByIdCategoriaTest() {
        Long id = 1L;
        Categoria categoria = createValidCategoria();
        categoria.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(categoria));
        Optional<Categoria> foundCategoria = service.getById(id);
        assertThat(foundCategoria.isPresent()).isTrue();
        assertThat(foundCategoria.get().getId()).isEqualTo(id);
        assertThat(foundCategoria.get().getNome()).isEqualTo(categoria.getNome());
    }

    @Test
    @DisplayName("Deve retornar em branco ao procurar uma categoria que não existe")
    public void notFoundByIdCategoriaTest() {
        Long id = 1L;
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
        Optional<Categoria> foundCategoria = service.getById(id);
        assertThat(foundCategoria.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Deve remover uma categoria")
    public void deleteCategoriaTest() {
        Categoria categoria = Categoria.builder().id(1L).build();
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.delete(categoria));
        Mockito.verify(repository, Mockito.times(1)).delete(categoria);
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar remover uma categoria inexistente")
    public void deleteInexistentCategoriaTest() {
        Categoria categoria = new Categoria();
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(categoria));
        Mockito.verify(repository, Mockito.never()).delete(categoria);
    }

    @Test
    @DisplayName("Deve atualizar uma categoria")
    public void updateCategoriaTest() {
        Long id = 1L;
        Categoria updatingCategoria = Categoria.builder().id(1L).build();
        Categoria updatedCategoria = createValidCategoria();
        updatedCategoria.setId(id);
        Mockito.when(repository.save(updatingCategoria)).thenReturn(updatedCategoria);
        Categoria categoria = service.update(updatingCategoria);
        assertThat(categoria.getId()).isEqualTo(updatedCategoria.getId());
        assertThat(categoria.getNome()).isEqualTo(updatedCategoria.getNome());
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar atualizar uma categoria inexistente")
    public void updateInexistentCategoriaTest() {
        Categoria categoria = new Categoria();
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(categoria));
        Mockito.verify(repository, Mockito.never()).save(categoria);
    }

    @Test
    @DisplayName("Deve filtrar uma categoria pelo nome")
    public void findCategoriaTest() {
        Categoria categoria = createValidCategoria();
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Categoria> lista = Arrays.asList(categoria);
        Page<Categoria> page = new PageImpl<Categoria>(lista, pageRequest, 1);
        Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class))).thenReturn(page);
        Page<Categoria> result = service.find(categoria, pageRequest);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    private Categoria createValidCategoria() {
        return Categoria.builder().nome("Categoria2").build();
    }
}
