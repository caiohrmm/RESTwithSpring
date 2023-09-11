package br.com.caiohenrique.services;

import br.com.caiohenrique.controllers.PersonController;
import br.com.caiohenrique.data.valueobjects.v1.PersonVO;
import br.com.caiohenrique.exceptions.RequiredObjectIsNullException;
import br.com.caiohenrique.exceptions.ResourceNotFoundException;
import br.com.caiohenrique.mapper.DozerMapper;
import br.com.caiohenrique.model.Person;
import br.com.caiohenrique.repositories.PersonRepository;
import br.com.caiohenrique.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class UserService implements UserDetailsService {

    private final Logger logger = Logger.getLogger(UserService.class.getName());


    // Posso utilizar a injeção de dependencias de duas maneiras
    // Por construtor ou Autowired.

    private final UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    // Find One Person
    public PersonVO findById(Long id) {
        logger.info("Finding one person...");
        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));
        return DozerMapper.parseObject(entity, PersonVO.class).add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Finding user "+username);
        var user = repository.findByUsername(username);

        if (user != null) {
            return user;
        }else {
            throw new UsernameNotFoundException("Username " + username + " not found!");
        }
    }
}