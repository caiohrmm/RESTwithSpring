package br.com.caiohenrique.services;

import br.com.caiohenrique.data.vo.v1.PersonVO;
import br.com.caiohenrique.exceptions.ResourceNotFoundException;
import br.com.caiohenrique.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class PersonService {

    private final Logger logger = Logger.getLogger(PersonService.class.getName());

    @Autowired
    PersonRepository repository;

    // Find One Person
    public PersonVO findById(Long id) {
        logger.info("Finding one person...");
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
    }

    // Find All Persons
    public List<PersonVO> findAllPersons() {
        logger.info("Finding all persons...");
        return repository.findAll();
    }

    // Creating person
    public PersonVO createPerson(PersonVO personVO) {
        logger.info("Creating one person...");
        // Conexao com o banco
        return repository.save(personVO);
    }

    // Updating person
    public PersonVO updatePerson(PersonVO personVO) {
        logger.info("Updating one person...");
        // Conexão com o banco
        PersonVO entity = repository.findById(personVO.getId()).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        entity.setFirstName(personVO.getFirstName());
        entity.setLastName(personVO.getLastName());
        entity.setAddress(personVO.getAddress());
        entity.setGender(personVO.getGender());

        return repository.save(entity);
    }

    // Delete person
    public void deletePersonById(Long id) {
        logger.info("Deleting one person...");
        PersonVO entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        repository.delete(entity);
    }

}