package br.com.caiohenrique.apispringboot.unittests.mockito.services;

import br.com.caiohenrique.apispringboot.unittests.mapper.mocks.MockBook;
import br.com.caiohenrique.data.valueobjects.v1.BookVO;
import br.com.caiohenrique.exceptions.RequiredObjectIsNullException;
import br.com.caiohenrique.model.Book;
import br.com.caiohenrique.repositories.BookRepository;
import br.com.caiohenrique.services.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    MockBook input;

    @InjectMocks
    private BookService service;

    @Mock
    BookRepository bookRepository;

    @BeforeEach
    void setUpMocks() throws Exception {
        input = new MockBook();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById() {
        // Primeira coisa eu preciso mockar um book
        Book book = input.mockEntity(1);
        book.setId(1L);

        // Quando o método repository for chamado, retornarei um mock.
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

        var result = service.findById(book.getId());
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getLinks());
        assertTrue(result.toString().contains("links: [</books/v1/1>;rel=\"self\"]"));
        assertEquals("Author Test1", result.getAuthor());
        assertEquals("Title Test1", result.getTitle());
        assertEquals(100.0, result.getPrice());
        assertEquals(new Date(1630738800000L), result.getLaunchDate());
    }

    @Test
    void createBook() {

        // Primeira coisa eu preciso mockar uma person
        Book book = input.mockEntity(1);
        Book persistedBook = book;


        // Primeiramente eu recebo um book sem ID, igual recebo pelo body
        // Quando envio ele pro banco é ele que seta o ID, entao farei isso manualmente.

        persistedBook.setId(1L);

        BookVO bookVO = input.mockVO(1);
        bookVO.setKey(1L);

        // Quando o método repository for chamado, retornarei um mock.
        when(bookRepository.save(book)).thenReturn(persistedBook);

        var result = service.createBook(bookVO);
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getLinks());
        assertTrue(result.toString().contains("links: [</books/v1/1>;rel=\"self\"]"));
        assertEquals("Author Test1", result.getAuthor());
        assertEquals("Title Test1", result.getTitle());
        assertEquals(100.0, result.getPrice());
        assertEquals(new Date(1630738800000L), result.getLaunchDate());

    }

    @Test
    void createWithNullBook() {
        // Garantir que lance uma exceção e armazená-la em um objeto
        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.createBook(null);
        });

        // Definir a mensagem de erro esperada
        String expectedMessage = "It is not allowed to persist a null object !";
        String actualMessage = exception.getMessage();


        assertTrue(actualMessage.contains(expectedMessage));
    }


    @Test
    void updateBook() {
        // Primeira coisa eu preciso mockar um book
        Book book = input.mockEntity(1);
        book.setId(1L);
        Book persistedBook = book;

        //persisted.setId(1L);


        // Quando envio ela pro banco é ele que seta o ID, entao farei isso manualmente.

        BookVO bookVO= input.mockVO(1);
        bookVO.setKey(1L);

        // Quando o método repository for chamado, retornarei um mock.
        // Quando o método repository for chamado, retornarei um mock.
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(persistedBook);

        var result = service.updateBook(bookVO);
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getLinks());
        assertTrue(result.toString().contains("links: [</books/v1/1>;rel=\"self\"]"));
        assertEquals("Author Test1", result.getAuthor());
        assertEquals("Title Test1", result.getTitle());
        assertEquals(100.0, result.getPrice());
        assertEquals(new Date(1630738800000L), result.getLaunchDate());
    }

    @Test
    void updateWithNullBook() {
        // Garantir que lance uma exceção e armazená-la em um objeto
        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.updateBook(null);
        });

        // Definir a mensagem de erro esperada
        String expectedMessage = "It is not allowed to persist a null object !";
        String actualMessage = exception.getMessage();


        assertTrue(actualMessage.contains(expectedMessage));
    }


    @Test
    void deleteBookById() {
        // Primeira coisa eu preciso mockar um book
        Book book = input.mockEntity(1);
        book.setId(1L);

        // Quando o método repository for chamado, retornarei um mock.
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        service.deleteBookById(book.getId());
    }
}