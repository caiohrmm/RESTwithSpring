package br.com.caiohenrique.apispringboot.unittests.mockito.services;

import br.com.caiohenrique.apispringboot.unittests.mapper.mocks.MockBook;
import br.com.caiohenrique.apispringboot.unittests.mapper.mocks.MockPerson;
import br.com.caiohenrique.model.Book;
import br.com.caiohenrique.model.Person;
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

import java.math.BigDecimal;
import java.util.Date;
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
        assertEquals(new BigDecimal("100"), result.getPrice());
        assertEquals(new Date(1630738800000L), result.getLaunchDate());
    }

    @Test
    void findAllBooks() {
    }

    @Test
    void createBook() {
    }

    @Test
    void updatePerson() {
    }

    @Test
    void deleteBookById() {
    }
}