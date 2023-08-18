package br.com.caiohenrique.repositories;

import br.com.caiohenrique.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

// A partir dessa interface consigo realizar todas as operacoes no meu banco
public interface PersonRepository extends JpaRepository<Person, Long> {
}
