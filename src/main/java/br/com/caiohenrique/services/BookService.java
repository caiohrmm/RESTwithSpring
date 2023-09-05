package br.com.caiohenrique.services;

import br.com.caiohenrique.controllers.BookController;
import br.com.caiohenrique.data.valueobjects.v1.BookVO;
import br.com.caiohenrique.exceptions.RequiredObjectIsNullException;
import br.com.caiohenrique.exceptions.ResourceNotFoundException;
import br.com.caiohenrique.mapper.DozerMapper;
import br.com.caiohenrique.model.Book;
import br.com.caiohenrique.model.Person;
import br.com.caiohenrique.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class BookService {
    private final Logger logger = Logger.getLogger(BookService.class.getName());

    @Autowired
    private BookRepository repository;

    // Find one Book
    public BookVO findById(Long id) {
        logger.info("Finding one book...");
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No book records found for this ID"));
        return DozerMapper.parseObject(entity, BookVO.class).add(linkTo(methodOn(BookController.class).findBookById(id)).withSelfRel());
    }

    // Find All Persons
    public List<BookVO> findAllBooks() {
        logger.info("Finding all books...");

        // Irei criar um loop para adicionar os links para cada objeto da lista.
        var books = DozerMapper.parseListObjects(repository.findAll(), BookVO.class);

        books.forEach(book -> book.add(linkTo(methodOn(BookController.class).findBookById(book.getKey())).withSelfRel()));
        return books;
    }

    // Creating person
    public BookVO createBook(BookVO bookVO) {

        if (bookVO == null) throw new RequiredObjectIsNullException();

        logger.info("Creating one book...");

        // Recebo um VO, preciso converter para entidade e depois que salvar passar para VO novamente.
        var entity
                = DozerMapper.parseObject(bookVO, Book.class);
        var valueObject = DozerMapper.parseObject(repository.save(entity), BookVO.class);
        valueObject.add(linkTo(methodOn(BookController.class).findBookById(valueObject.getKey())).withSelfRel());
        return valueObject;
    }

    // Updating person
    public BookVO updateBook(BookVO bookVO) {

        if (bookVO == null) throw new RequiredObjectIsNullException();

        logger.info("Updating one book...");
        // Conexão com o banco
        Book entity = repository.findById(bookVO.getKey()).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        entity.setAuthor(bookVO.getAuthor());
        entity.setTitle(bookVO.getTitle());
        entity.setLaunchDate(bookVO.getLaunchDate());

        var valueObject = DozerMapper.parseObject(repository.save(entity), BookVO.class);
        valueObject.add(linkTo(methodOn(BookController.class).findBookById(valueObject.getKey())).withSelfRel());
        return valueObject;
    }

    // Delete person
    public void deleteBookById(Long id) {
        logger.info("Deleting one book...");
        Book entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        repository.delete(entity);
    }



}
