package br.com.caiohenrique.mapper;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

import java.util.ArrayList;
import java.util.List;

// Converte VO em entidades e entidades em objetos.
public class DozerMapper {


    private static Mapper mapper = DozerBeanMapperBuilder.buildDefault();


     public static <O,D> D parseObject(O origin,Class<D> destination) {
        return mapper.map(origin, destination);
     }

     // Mesmo processo só que agora para listas
     public static <O,D> List<D> parseListObjects(List<O> origin, Class<D> destination) {
         List<D> destinationObjets = new ArrayList<D>();
         for (O o: origin
              ) {
             destinationObjets.add(mapper.map(origin, destination));
         }
         return destinationObjets;
     }

}
