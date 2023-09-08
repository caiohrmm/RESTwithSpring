package br.com.caiohenrique.controllers;

import br.com.caiohenrique.data.valueobjects.v1.BookVO;
import br.com.caiohenrique.services.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<BookVO> findAllBooks(){
        return service.findAllBooks();
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
