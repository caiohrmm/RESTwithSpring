package br.com.caiohenrique.apispringboot.integrationtests.repositories;
// Esses testes testam apenas os repositórios, nao incluem requests HTTP.

import br.com.caiohenrique.apispringboot.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.caiohenrique.model.Person;
import br.com.caiohenrique.repositories.PersonRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private PersonRepository repository;

    private static Person person;

    @BeforeAll
    public static void beforeAll() {
        person = new Person();
    }

    @Test
    @Order(1)
    public void testFindPersonByFirstname() throws IOException {

        String firstName = "be";

        // Para transformar o valor criado em Vo e conseguir ler ele.
        Pageable pageable = PageRequest.of(1, 15,
                Sort.by(Sort.Direction.ASC, "firstName"));

        person = repository.findPersonByName(firstName, pageable).getContent().get(0);

        assertEquals(person.getId(), 438);

        assertEquals("Berkeley", person.getFirstName());
        assertEquals("Button", person.getLastName());
        assertEquals("77 Mifflin Plaza", person.getAddress());
        assertEquals("Male", person.getGender());
        assertTrue(person.getEnabled());
    }



}
