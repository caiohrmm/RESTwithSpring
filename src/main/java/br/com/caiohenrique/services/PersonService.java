package br.com.caiohenrique.services;

import br.com.caiohenrique.controllers.PersonController;
import br.com.caiohenrique.data.valueobjects.v1.PersonVO;
import br.com.caiohenrique.exceptions.RequiredObjectIsNullException;
import br.com.caiohenrique.exceptions.ResourceNotFoundException;
import br.com.caiohenrique.mapper.DozerMapper;
import br.com.caiohenrique.model.Person;
import br.com.caiohenrique.repositories.PersonRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class PersonService {

    private final Logger logger = Logger.getLogger(PersonService.class.getName());

    @Autowired
    private PersonRepository repository;

    @Autowired
    PagedResourcesAssembler<PersonVO> assembler;



    // Find One Person
    public PersonVO findById(Long id) {
        logger.info("Finding one person...");
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        return DozerMapper.parseObject(entity, PersonVO.class).add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
    }

    // Find All Persons
    public PagedModel<EntityModel<PersonVO>> findAllPersons(Pageable pageable) {
        logger.info("Finding all persons...");

        // Irei criar um loop para adicionar os links para cada objeto da lista.
        var pagePersons = repository.findAll(pageable);
        var listVoPersons = pagePersons.map(p -> DozerMapper.parseObject(p, PersonVO.class));

        listVoPersons.map(p ->  p.add(linkTo(methodOn(PersonController.class)
                .findById(p.getKey())).withSelfRel()));

        Link link = linkTo(methodOn(PersonController.class)
                .findAllPersons(pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();

        return assembler.toModel(listVoPersons, link);
    }


    // Find Person By Firstname
    public PagedModel<EntityModel<PersonVO>> findPersonByName(Pageable pageable, String firstName) {
        logger.info("Finding person by " +firstName );

        // Irei criar um loop para adicionar os links para cada objeto da lista.
        var listVoPersons = repository.findPersonByName(firstName, pageable)
                .map(p -> DozerMapper.parseObject(p, PersonVO.class));

        listVoPersons.map(p ->  p.add(linkTo(methodOn(PersonController.class)
                .findById(p.getKey())).withSelfRel()));

        Link link = linkTo(methodOn(PersonController.class)
                .findAllPersons(pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();

        return assembler.toModel(listVoPersons, link);
    }

    // Creating person
    public PersonVO createPerson(PersonVO personVO) {

        if (personVO == null) throw new RequiredObjectIsNullException();

        logger.info("Creating one person...");

        // Recebo um VO, preciso converter para entidade e depois que salvar passar para VO novamente.
        var entity = DozerMapper.parseObject(personVO, Person.class);
        var valueObject = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
        valueObject.add(linkTo(methodOn(PersonController.class).findById(valueObject.getKey())).withSelfRel());
        return valueObject;
    }

    // Updating person
    public PersonVO updatePerson(PersonVO personVO) {

        if (personVO == null) throw new RequiredObjectIsNullException();

        logger.info("Updating one person...");
        // Conexão com o banco
        Person entity = repository.findById(personVO.getKey()).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        entity.setFirstName(personVO.getFirstName());
        entity.setLastName(personVO.getLastName());
        entity.setAddress(personVO.getAddress());
        entity.setGender(personVO.getGender());

        var valueObject = DozerMapper.parseObject(repository.save(entity), PersonVO.class);
        valueObject.add(linkTo(methodOn(PersonController.class).findById(valueObject.getKey())).withSelfRel());
        return valueObject;
    }

    // Delete person
    public void deletePersonById(Long id) {
        logger.info("Deleting one person...");
        Person entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        repository.delete(entity);
    }
    @Transactional // Preciso da annotation para concluir o método customizado do repositório.
    public PersonVO disablePersonById(Long id) {

        logger.info("Disabling one person with id "+id);

        // Desabilito o enabled da tabela Person
        repository.disablePerson(id);

        // Após desabilitado, retorno ao cliente o VO do objeto já alterado.
        logger.info("Finding one person...");
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        return DozerMapper.parseObject(entity, PersonVO.class).add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());

    }

}