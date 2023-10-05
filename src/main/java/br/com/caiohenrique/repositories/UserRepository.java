package br.com.caiohenrique.repositories;

import br.com.caiohenrique.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {


    // Quando é SELECT não preciso de anotação do ACID como Modifying, somente nos outros verbos do CRUD.
    @Query("SELECT u FROM User u WHERE u.username =:username")
    User findByUsername(@Param("username") String username);
    // Nao confundir o Query do JPA com o do SQL, esse busca os dados do objeto e nao dá tabela.

}
