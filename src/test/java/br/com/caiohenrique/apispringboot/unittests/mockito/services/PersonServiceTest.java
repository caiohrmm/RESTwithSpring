package br.com.caiohenrique.apispringboot.unittests.mockito.services;

import br.com.caiohenrique.apispringboot.unittests.mapper.mocks.MockPerson;
import br.com.caiohenrique.data.valueobjects.v1.PersonVO;
import br.com.caiohenrique.exceptions.RequiredObjectIsNullException;
import br.com.caiohenrique.model.Person;
import br.com.caiohenrique.repositories.PersonRepository;
import br.com.caiohenrique.services.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;


import static org.junit.jupiter.api.Assertions.*;

// Definir ciclo de vida.
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    MockPerson input;

    @InjectMocks
    private PersonService service;

    @Mock
    PersonRepository personRepository;




    @BeforeEach
    void setUpMocks() throws Exception {
        input = new MockPerson();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById() {
        // Primeira coisa eu preciso mockar uma person
        Person person = input.mockEntity(1);
        person.setId(1L);

        // Quando o método repository for chamado, retornarei um mock.
        when(personRepository.findById(person.getId())).thenReturn(Optional.of(person));

        var result = service.findById(person.getId());
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getLinks());
        assertTrue(result.toString().contains("links: [</persons/v1/1>;rel=\"self\"]"));
        assertEquals("Addres Test1", result.getAddress());
        assertEquals("Female", result.getGender());
        assertEquals("First Name Test1", result.getFirstName());
        assertEquals("Last Name Test1", result.getLastName());

    }

    @Test
    void findAllPersons() {
        List<Person> list = input.mockEntityList();

        when(personRepository.findAll()).thenReturn(list);

        var people = service.findAllPersons();

        assertNotNull(people);
        assertEquals(14, people.size());

        var personOne = people.get(1);

        assertNotNull(personOne);
        assertNotNull(personOne.getKey());
        assertNotNull(personOne.getLinks());

        assertTrue(personOne.toString().contains("links: [</persons/v1/1>;rel=\"self\"]"));
        assertEquals("Addres Test1", personOne.getAddress());
        assertEquals("First Name Test1", personOne.getFirstName());
        assertEquals("Last Name Test1", personOne.getLastName());
        assertEquals("Female", personOne.getGender());

        var personFour = people.get(4);

        assertNotNull(personFour);
        assertNotNull(personFour.getKey());
        assertNotNull(personFour.getLinks());

        assertTrue(personFour.toString().contains("links: [</persons/v1/4>;rel=\"self\"]"));
        assertEquals("Addres Test4", personFour.getAddress());
        assertEquals("First Name Test4", personFour.getFirstName());
        assertEquals("Last Name Test4", personFour.getLastName());
        assertEquals("Male", personFour.getGender());

        var personSeven = people.get(7);

        assertNotNull(personSeven);
        assertNotNull(personSeven.getKey());
        assertNotNull(personSeven.getLinks());

        assertTrue(personSeven.toString().contains("links: [</persons/v1/7>;rel=\"self\"]"));
        assertEquals("Addres Test7", personSeven.getAddress());
        assertEquals("First Name Test7", personSeven.getFirstName());
        assertEquals("Last Name Test7", personSeven.getLastName());
        assertEquals("Female", personSeven.getGender());

    }

    @Test
    void createPerson() {


        // Primeira coisa eu preciso mockar uma person
        Person person = input.mockEntity(1);
        Person persisted = person;


        // Primeiramente eu recebo uma person sem ID, igual recebo pelo body
        // Quando envio ela pro banco é ele que seta o ID, entao farei isso manualmente.

        persisted.setId(1L);

        PersonVO personVo = input.mockVO(1);
        personVo.setKey(1L);

        // Quando o método repository for chamado, retornarei um mock.
        when(personRepository.save(person)).thenReturn(persisted);

        var result = service.createPerson(personVo);
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getLinks());
        assertTrue(result.toString().contains("links: [</persons/v1/1>;rel=\"self\"]"));
        assertEquals("Addres Test1", result.getAddress());
        assertEquals("Female", result.getGender());
        assertEquals("First Name Test1", result.getFirstName());
        assertEquals("Last Name Test1", result.getLastName());


    }
    @Test
    void createWithNullPerson() {
        // Garantir que lance uma exceção e armazená-la em um objeto
        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.createPerson(null);
        });

        // Definir a mensagem de erro esperada
        String expectedMessage = "It is not allowed to persist a null object !";
        String actualMessage = exception.getMessage();


        assertTrue(actualMessage.contains(expectedMessage));
    }



    @Test
    void updatePerson() {
        // Primeira coisa eu preciso mockar uma person
        Person person = input.mockEntity(1);
        person.setId(1L);
        Person persisted = person;
        persisted.setId(1L);


        // Primeiramente eu recebo uma person sem ID, igual recebo pelo body
        // Quando envio ela pro banco é ele que seta o ID, entao farei isso manualmente.

        PersonVO personVo = input.mockVO(1);
        personVo.setKey(1L);

        // Quando o método repository for chamado, retornarei um mock.
        // Quando o método repository for chamado, retornarei um mock.
        when(personRepository.findById(person.getId())).thenReturn(Optional.of(person));
        when(personRepository.save(person)).thenReturn(persisted);

        var result = service.updatePerson(personVo);
        assertNotNull(result);
        assertNotNull(result.getKey());
        assertNotNull(result.getLinks());
        assertTrue(result.toString().contains("links: [</persons/v1/1>;rel=\"self\"]"));
        assertEquals("Addres Test1", result.getAddress());
        assertEquals("Female", result.getGender());
        assertEquals("First Name Test1", result.getFirstName());
        assertEquals("Last Name Test1", result.getLastName());
    }

    @Test
    void updateWithNullPerson() {
        // Garantir que lance uma exceção e armazená-la em um objeto
        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.updatePerson(null);
        });

        // Definir a mensagem de erro esperada
        String expectedMessage = "It is not allowed to persist a null object !";
        String actualMessage = exception.getMessage();


        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void deletePersonById() {
        // Primeira coisa eu preciso mockar uma person
        Person person = input.mockEntity(1);
        person.setId(1L);

        // Quando o método repository for chamado, retornarei um mock.
        when(personRepository.findById(person.getId())).thenReturn(Optional.of(person));
        service.deletePersonById(person.getId());

    }
}