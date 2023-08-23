package br.com.caiohenrique.controllers;

import br.com.caiohenrique.data.vo.v1.PersonVO;
import br.com.caiohenrique.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static br.com.caiohenrique.util.MediaType.APPLICATION_JSON;
import static br.com.caiohenrique.util.MediaType.APPLICATION_XML;
import static br.com.caiohenrique.util.MediaType.APPLICATION_YML;

// Preciso sempre da anotação do Spring para reconhecer que essa é uma classe de controle do REST.
@RestController
@RequestMapping("/persons/v1")
public class PersonController {

    @Autowired
    private PersonService service;

    @GetMapping(produces = { APPLICATION_JSON, APPLICATION_XML, APPLICATION_YML})
    public List<PersonVO> findAllPersons() {
        return service.findAllPersons();
    }

    @GetMapping(value = "/{id}", produces = { APPLICATION_JSON, APPLICATION_XML, APPLICATION_YML})
    public PersonVO findById(@PathVariable(value = "id") Long id) throws Exception {
        return service.findById(id);
    }

    @PostMapping(consumes = { APPLICATION_JSON, APPLICATION_XML, APPLICATION_JSON}, produces = { APPLICATION_JSON, APPLICATION_XML, APPLICATION_YML})
    public PersonVO createPerson(@RequestBody PersonVO personVO) {
        return service.createPerson(personVO);
    }

    @PutMapping(consumes = { APPLICATION_JSON, APPLICATION_XML, APPLICATION_JSON}, produces = { APPLICATION_JSON, APPLICATION_XML, APPLICATION_YML})
    public PersonVO updatePerson(@RequestBody PersonVO personVO) {
        return service.updatePerson(personVO);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable(value = "id") Long id) {
        service.deletePersonById(id);
        // Preciso retornar status code 204.
        return ResponseEntity.noContent().build();
    }

}
