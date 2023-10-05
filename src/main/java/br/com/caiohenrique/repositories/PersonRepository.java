package br.com.caiohenrique.repositories;

import br.com.caiohenrique.model.Person;
import br.com.caiohenrique.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// A partir dessa interface consigo realizar todas as operações no meu banco
public interface PersonRepository extends JpaRepository<Person, Long> {

    // Quando é SELECT não preciso de anotação do ACID como Modifying, somente nos outros verbos do CRUD.
    @Modifying // Preciso para fazer a query jpa update.
    @Query("UPDATE Person p SET p.enabled = false WHERE p.id = :id")
    void disablePerson(@Param("id") Long id);
    // Nao confundir o Query do JPA com o do SQL, esse busca os dados do objeto e nao dá tabela.
}
