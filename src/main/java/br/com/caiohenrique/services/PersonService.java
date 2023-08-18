package br.com.caiohenrique.services;

import br.com.caiohenrique.model.Person;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

@Service
public class PersonService {
    private final AtomicLong counter = new AtomicLong();
    private final Logger logger = Logger.getLogger(PersonService.class.getName());

    // Find One Person
    public Person findById(String id) {
        logger.info("Finding one person...");
        Person person = new Person();
        person.setId(counter.incrementAndGet());
        person.setFirstName("Caio");
        person.setLastName("Martins");
        person.setAddress("Sodrélia");
        person.setGender("Male");
        return person;
    }

    // Find All Persons
    public List<Person> findAllPersons() {
        logger.info("Finding all persons...");
        List<Person> persons = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            Person person = mockPerson(i);
            persons.add(person);
        }
        return persons;
    }

    // Creating person
    public Person createPerson(Person person) {
        logger.info("Creating one person...");
        // Conexao com o banco
        return person;
    }

    // Updating person
    public Person updatePerson(Person person) {
        logger.info("Updating one person...");
        // Conexao com o banco
        return person;
    }

    // Updating person
    public void deletePersonById(String id) {
        logger.info("Deleting one person...");
    }

    private Person mockPerson(int i) {
        Person person = new Person();
        person.setId(counter.incrementAndGet());
        person.setFirstName("Person "+i);
        person.setLastName("Last name "+i);
        person.setAddress("Brazil "+i);
        person.setGender("Male");
        return person;
    }

}