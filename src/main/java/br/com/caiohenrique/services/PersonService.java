package br.com.caiohenrique.services;

import br.com.caiohenrique.exceptions.ResourceNotFoundException;
import br.com.caiohenrique.model.Person;
import br.com.caiohenrique.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

@Service
public class PersonService {

    private final Logger logger = Logger.getLogger(PersonService.class.getName());

    PersonRepository repository;

    // Find One Person
    public Person findById(Long id) {
        logger.info("Finding one person...");
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
    }

    // Find All Persons
    public List<Person> findAllPersons() {
        logger.info("Finding all persons...");
        return repository.findAll();
    }

    // Creating person
    public Person createPerson(Person person) {
        logger.info("Creating one person...");
        // Conexao com o banco
        return repository.save(person);
    }

    // Updating person
    public Person updatePerson(Person person) {
        logger.info("Updating one person...");
        // Conexão com o banco
        Person entity = repository.findById(person.getId()).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        return repository.save(person);
    }

    // Delete person
    public void deletePersonById(Long id) {
        logger.info("Deleting one person...");
        Person entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        repository.delete(entity);

    }

}