package br.com.caiohenrique.services;

import br.com.caiohenrique.data.vo.v1.PersonVO;
import br.com.caiohenrique.exceptions.ResourceNotFoundException;
import br.com.caiohenrique.mapper.ModelMapper;
import br.com.caiohenrique.model.Person;
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
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        return ModelMapper.parseObject(entity, PersonVO.class);
    }

    // Find All Persons
    public List<PersonVO> findAllPersons() {
        logger.info("Finding all persons...");
        return ModelMapper.parseListObjects(repository.findAll(), PersonVO.class);
    }

    // Creating person
    public PersonVO createPerson(PersonVO personVO) {
        logger.info("Creating one person...");

        // Recebo um VO, preciso converter para entidade e depois que salvar passar para VO novamente.
        var entity = ModelMapper.parseObject(personVO, Person.class);
        var valueObject = ModelMapper.parseObject(repository.save(entity), PersonVO.class);
        return valueObject;
    }

    // Updating person
    public PersonVO updatePerson(PersonVO personVO) {
        logger.info("Updating one person...");
        // Conexão com o banco
        Person entity = repository.findById(personVO.getId()).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        entity.setFirstName(personVO.getFirstName());
        entity.setLastName(personVO.getLastName());
        entity.setAddress(personVO.getAddress());
        entity.setGender(personVO.getGender());

        var valueObject = ModelMapper.parseObject(repository.save(entity), PersonVO.class);
        return valueObject;
    }

    // Delete person
    public void deletePersonById(Long id) {
        logger.info("Deleting one person...");
        Person entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        repository.delete(entity);
    }

}