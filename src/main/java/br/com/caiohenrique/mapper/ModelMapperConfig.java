package br.com.caiohenrique.mapper;

import br.com.caiohenrique.data.valueobjects.v1.PersonVO;
import br.com.caiohenrique.model.Person;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(Person.class, PersonVO.class).addMapping(Person::getId, PersonVO::setKey);

        return modelMapper;
    }
}
