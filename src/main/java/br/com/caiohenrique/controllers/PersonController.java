package br.com.caiohenrique.controllers;

import br.com.caiohenrique.data.valueobjects.v1.PersonVO;
import br.com.caiohenrique.security.jwt.JwtTokenProvider;
import br.com.caiohenrique.services.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static br.com.caiohenrique.util.MediaType.APPLICATION_JSON;
import static br.com.caiohenrique.util.MediaType.APPLICATION_XML;
import static br.com.caiohenrique.util.MediaType.APPLICATION_YML;

// Preciso sempre da anotação do Spring para reconhecer que essa é uma classe de controle do REST.
@RestController
@RequestMapping("/persons/v1")
@Tag(name = "People", description = "Endpoints for Managing People")
public class PersonController {

    @Autowired
    private PersonService service;

    @GetMapping(produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_YML})
    @Operation(summary = "Finds all People", description = "Find all people", tags = {"People"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = PersonVO.class))
                                    ), @Content(
                                    mediaType = "application/xml",
                                    array = @ArraySchema(schema = @Schema(implementation = PersonVO.class))
                            ),
                                    @Content(
                                            mediaType = "application/x-yaml",
                                            array = @ArraySchema(schema = @Schema(implementation = PersonVO.class))
                                    )
                            }),

                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content),
            })

    // Não retorno mais uma lista e sim uma paginação de VOs.
    public ResponseEntity<PagedModel<EntityModel<PersonVO>>> findAllPersons(
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
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));

            return ResponseEntity.ok(service.findAllPersons(pageable));
    }

    @GetMapping(value = "/findByName/{firstName}",produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_YML})
    @Operation(summary = "Finds people by name", description = "Find people by name", tags = {"People"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            mediaType = "application/json",
                                            array = @ArraySchema(schema = @Schema(implementation = PersonVO.class))
                                    ), @Content(
                                    mediaType = "application/xml",
                                    array = @ArraySchema(schema = @Schema(implementation = PersonVO.class))
                            ),
                                    @Content(
                                            mediaType = "application/x-yaml",
                                            array = @ArraySchema(schema = @Schema(implementation = PersonVO.class))
                                    )
                            }),

                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content),
            })

    // Não retorno mais uma lista e sim uma paginação de VOs.
    public ResponseEntity<PagedModel<EntityModel<PersonVO>>> findPersonByName(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam (value = "size", defaultValue = "15") Integer size,
            @RequestParam (value = "direction", defaultValue = "asc") String direction,
            @PathVariable(value = "firstName") String firstName
    ) {
            // Não consigo usar o direction como String no parâmetro do pageable, farei conversão

            var sortDirection = "desc".equalsIgnoreCase(direction) ?
                    Sort.Direction.DESC :
                    Sort.Direction.ASC;
            // Fiz uma operação com operadores ternários, mas é um if comum.


            // Configuração da página como parâmetro do findAll.
            // Sort.by(direction, atributo da minha entidade a qual quero ordenar)
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "firstName"));

            return ResponseEntity.ok(service.findPersonByName(pageable, firstName));
    }

    @GetMapping(value = "/{id}", produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_YML})
    @Operation(summary = "Find a Person", description = "Find a person", tags = {"People"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            schema = @Schema(implementation = PersonVO.class)
                                    )
                            }),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content),
            })
    public PersonVO findById(@PathVariable(value = "id") Long id) {
        return service.findById(id);
    }

    @PostMapping(consumes = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_YML}, produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_YML})
    @Operation(summary = "Add a Person", description = "Add a new person by passing in a JSON, XML or YML", tags = {"People"},
            responses = {
                    @ApiResponse(description = "Created", responseCode = "200",
                            content = {
                                    @Content(
                                            schema = @Schema(implementation = PersonVO.class)
                                    )
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content),
            })
    public PersonVO createPerson(@RequestBody PersonVO personVO) {
        return service.createPerson(personVO);
    }

    @PutMapping(consumes = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_YML}, produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_YML})
    @Operation(summary = "Update a Person", description = "Update a persisted person by passing in a JSON, XML or YML", tags = {"People"},
            responses = {
                    @ApiResponse(description = "Updated", responseCode = "200",
                            content = {
                                    @Content(
                                            schema = @Schema(implementation = PersonVO.class)
                                    )
                            }),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content),
            })
    public PersonVO updatePerson(@RequestBody PersonVO personVO) {
        return service.updatePerson(personVO);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete a Person", description = "Delete person", tags = {"People"},
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
    public ResponseEntity<?> deleteById(@PathVariable(value = "id") Long id) {
        service.deletePersonById(id);
        // Preciso retornar status code 204.
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/disableperson/{id}", produces = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_YML})
    @Operation(summary = "Disable a Person with your Id", description = "Disable a Person", tags = {"People"},
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200",
                            content = {
                                    @Content(
                                            schema = @Schema(implementation = PersonVO.class)
                                    )
                            }),
                    @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content),
            })
    public PersonVO disablePersonById(@PathVariable(value = "id") Long id) {

            return service.disablePersonById(id);

    }

}
