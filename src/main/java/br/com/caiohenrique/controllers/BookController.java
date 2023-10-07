package br.com.caiohenrique.controllers;

import br.com.caiohenrique.data.valueobjects.v1.BookVO;
import br.com.caiohenrique.data.valueobjects.v1.PersonVO;
import br.com.caiohenrique.services.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static br.com.caiohenrique.util.MediaType.*;

// @CrossOrigin
// Permitir cors para todo o controller de todas as origins
@RestController
@RequestMapping("/books/v1")
@Tag(name = "Library", description = "Endpoints for Managing Library")
public class BookController {
    @Autowired
    private BookService service;

    // @CrossOrigin(origins = {"http://localhost:8080", "https://chrm.com.br"})
    @GetMapping(produces = { APPLICATION_JSON, APPLICATION_XML, APPLICATION_YML})
    @Operation(summary = "Finds all Books", description = "Find all books", tags = {"Library"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = BookVO.class))
                            ), @Content(
                            mediaType = "application/xml",
                            array = @ArraySchema(schema = @Schema(implementation = BookVO.class))
                    ),
                            @Content(
                                    mediaType = "application/x-yaml",
                                    array = @ArraySchema(schema = @Schema(implementation = BookVO.class))
                            )
                    }),

                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content),
            })

    public ResponseEntity<PagedModel<EntityModel<BookVO>>> findAllBooks(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam (value = "size", defaultValue = "15") Integer size,
            @RequestParam (value = "direction", defaultValue = "asc") String direction
    ) {
        // Não consigo usar o direction como String no parâmetro do pageable, farei conversão

        var sortDirection = "desc".equalsIgnoreCase(direction) ?
                Sort.Direction.DESC :
                Sort.Direction.ASC;
        // Fiz uma operação com operadores ternários, mas é um if comum.


        // Configuração da página como parâmetro do findAll.
        // Sort.by(direction, atributo da minha entidade a qual quero ordenar)
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "author"));

        return ResponseEntity.ok(service.findAllBooks(pageable));
    }


    @GetMapping(value = "/{id}", produces = { APPLICATION_JSON, APPLICATION_XML, APPLICATION_YML})
    @Operation(summary = "Find a book", description = "Find a book", tags = {"Library"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            schema = @Schema(implementation = BookVO.class)
                                    )
                            }),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content),
            })
    public BookVO findBookById(@PathVariable(value = "id") Long id){
        return service.findById(id);
    }

    @PostMapping(consumes = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_YML}, produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_YML})
    @Operation(summary = "Add a book", description = "Add a new book by passing in a JSON, XML or YML", tags = {"Library"},
            responses = {
                    @ApiResponse(description = "Created", responseCode = "200",
                            content = {
                                    @Content(
                                            schema = @Schema(implementation = BookVO.class)
                                    )
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content),
            })
    public BookVO createBook(@RequestBody BookVO bookVO){
        return service.createBook(bookVO);
    }

    @PutMapping(consumes = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_YML}, produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_YML})
    @Operation(summary = "Update a book", description = "Update a persisted book by passing in a JSON, XML or YML", tags = {"Library"},
            responses = {
                    @ApiResponse(description = "Updated", responseCode = "200",
                            content = {
                                    @Content(
                                            schema = @Schema(implementation = BookVO.class)
                                    )
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content),
            })
    public BookVO updateBook(@RequestBody BookVO bookVO){
        return service.updateBook(bookVO);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete a Book", description = "Delete a book", tags = {"Library"},
            responses = {
                    @ApiResponse(description = "No Content", responseCode = "204",
                            content = {
                                    @Content()
                            }),

                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content),
            })
    public ResponseEntity<?> deleteBookById(@PathVariable("id") Long id) {
        service.deleteBookById(id);
        return ResponseEntity.noContent().build();
    }
}
