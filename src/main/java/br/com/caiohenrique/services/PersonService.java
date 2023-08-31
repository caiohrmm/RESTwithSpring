package br.com.caiohenrique.services;

import br.com.caiohenrique.controllers.PersonController;
import br.com.caiohenrique.data.valueobjects.v1.PersonVO;
import br.com.caiohenrique.exceptions.ResourceNotFoundException;
import br.com.caiohenrique.model.Person;
import br.com.caiohenrique.repositories.PersonRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class PersonService {

    private final Logger logger = Logger.getLogger(PersonService.class.getName());

    @Autowired
    private PersonRepository repository;

    @Autowired
    private ModelMapper modelMapper;


    // Find One Person
    public PersonVO findById(Long id) {
        logger.info("Finding one person...");
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        return modelMapper.map(entity, PersonVO.class).add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
    }

    // Find All Persons
    public List<PersonVO> findAllPersons() {
        logger.info("Finding all persons...");

        // Irei criar um loop para adicionar os links para cada objeto da lista.
        var persons = repository.findAll().stream().map(person -> modelMapper.map(person, PersonVO.class)).collect(Collectors.toList());

        persons.stream().forEach(person -> person.add(linkTo(methodOn(PersonController.class).findById(person.getKey())).withSelfRel()));
        return persons;
    }

    // Creating person
    public PersonVO createPerson(PersonVO personVO) {
        logger.info("Creating one person...");

        // Recebo um VO, preciso converter para entidade e depois que salvar passar para VO novamente.
        var entity = modelMapper.map(personVO, Person.class);
        var valueObject = modelMapper.map(repository.save(entity), PersonVO.class);
        valueObject.add(linkTo(methodOn(PersonController.class).findById(valueObject.getKey())).withSelfRel());
        return valueObject;
    }

    // Updating person
    public PersonVO updatePerson(PersonVO personVO) {
        logger.info("Updating one person...");
        // Conexão com o banco
        Person entity = repository.findById(personVO.getKey()).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        entity.setFirstName(personVO.getFirstName());
        entity.setLastName(personVO.getLastName());
        entity.setAddress(personVO.getAddress());
        entity.setGender(personVO.getGender());

        var valueObject = modelMapper.map(repository.save(entity), PersonVO.class);
        valueObject.add(linkTo(methodOn(PersonController.class).findById(valueObject.getKey())).withSelfRel());
        return valueObject;
    }

    // Delete person
    public void deletePersonById(Long id) {
        logger.info("Deleting one person...");
        Person entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        repository.delete(entity);
    }

}