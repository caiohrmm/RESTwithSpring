package br.com.caiohenrique.controllers;

import br.com.caiohenrique.model.Person;
import br.com.caiohenrique.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

// Preciso sempre da anotação do Spring para reconhecer que essa é uma classe de controle do REST.
@RestController
@RequestMapping("/persons")
public class PersonController {

    @Autowired
    private PersonService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Person> findAllPersons() {
        return service.findAllPersons();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Person findById(@PathVariable(value = "id") Long id) throws Exception {
        return service.findById(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Person createPerson(@RequestBody Person person) {
        return service.createPerson(person);
    }

    @PutMapping (consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Person updatePerson(@RequestBody Person person) {
        return service.updatePerson(person);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteById(@PathVariable(value = "id") Long id) {
        service.deletePersonById(id);
    }

}
