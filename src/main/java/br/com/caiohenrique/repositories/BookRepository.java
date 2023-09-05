package br.com.caiohenrique.repositories;

import br.com.caiohenrique.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository <Book, Long> {
}
