package br.com.caiohenrique.controllers;

import br.com.caiohenrique.data.valueobjects.v1.BookVO;
import br.com.caiohenrique.data.valueobjects.v1.PersonVO;
import br.com.caiohenrique.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static br.com.caiohenrique.util.MediaType.APPLICATION_JSON;
import static br.com.caiohenrique.util.MediaType.APPLICATION_XML;
import static br.com.caiohenrique.util.MediaType.APPLICATION_YML;

import static br.com.caiohenrique.util.MediaType.*;

@RestController
@RequestMapping("/books/v1")
public class BookController {
    @Autowired
    private BookService service;

    @GetMapping(produces = { APPLICATION_JSON, APPLICATION_XML, APPLICATION_YML})
    public List<BookVO> findAllBooks(){
        return service.findAllBooks();
    }


    @GetMapping(value = "/{id}", produces = { APPLICATION_JSON, APPLICATION_XML, APPLICATION_YML})
    public BookVO findBookById(@PathVariable(value = "id") Long id){
        return service.findById(id);
    }

    @PostMapping(produces = { APPLICATION_JSON, APPLICATION_XML, APPLICATION_YML}, consumes = { APPLICATION_JSON, APPLICATION_XML, APPLICATION_YML})
    public BookVO createBook(@RequestBody BookVO bookVO){
        return service.createBook(bookVO);
    }

    @PutMapping(produces = { APPLICATION_JSON, APPLICATION_XML, APPLICATION_YML}, consumes = { APPLICATION_JSON, APPLICATION_XML, APPLICATION_YML})
    public BookVO updateBook(@RequestBody BookVO bookVO){
        return updateBook(bookVO);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteBookById(@PathVariable("id") Long id) {
        service.deleteBookById(id);
        return ResponseEntity.noContent().build();
    }
}
