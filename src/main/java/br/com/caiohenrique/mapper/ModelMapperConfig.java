package br.com.caiohenrique.mapper;

import br.com.caiohenrique.data.valueobjects.v1.PersonVO;
import br.com.caiohenrique.model.Person;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Service
public class ModelMapperConfig {

    private static final ModelMapper modelMapper = new ModelMapper();

    @Bean
    public ModelMapper modelMapper() {
        modelMapper.typeMap(Person.class, PersonVO.class).addMapping(Person::getId, PersonVO::setKey);

        return modelMapper;
    }


    public static <O, D> D parseObject(O origin, Class<D> destination) {
        return modelMapper.map(origin, destination);
    }


    public static <O, D> List<D> parseListObjects(List<O> origin, Class<D> destination) {
        List<D> destinationObjects = new ArrayList<D>();
        for (O o : origin) {
            destinationObjects.add(modelMapper.map(o, destination));
        }
        return destinationObjects;
    }
}
