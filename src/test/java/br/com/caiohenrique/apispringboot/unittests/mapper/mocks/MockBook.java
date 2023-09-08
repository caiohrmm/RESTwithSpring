package br.com.caiohenrique.apispringboot.unittests.mapper.mocks;

import br.com.caiohenrique.data.valueobjects.v1.BookVO;
import br.com.caiohenrique.data.valueobjects.v1.PersonVO;
import br.com.caiohenrique.model.Book;
import br.com.caiohenrique.model.Person;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MockBook {


    public Book mockEntity() {
        return mockEntity(0);
    }
    
    public BookVO mockVO() {
        return mockVO(0);
    }
    
    public List<Book> mockEntityList() {
        List<Book> books = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            books.add(mockEntity(i));
        }
        return books;
    }

    public List<BookVO> mockVOList() {
        List<BookVO> books = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            books.add(mockVO(i));
        }
        return books;
    }
    
    public Book mockEntity(Integer number) {
        Book book = new Book();
        book.setLaunchDate(new Date(1630738800000L));
        book.setTitle("Title Test" + number);
        book.setId(number.longValue());
        book.setAuthor("Author Test" + number);
        book.setPrice(100.0);
        return book;
    }

    public BookVO mockVO(Integer number) {
        BookVO bookVO = new BookVO();
        Book book = new Book();
        bookVO.setLaunchDate(new Date(1630738800000L));
        bookVO.setTitle("Title Test" + number);
        bookVO.setKey(number.longValue());
        bookVO.setAuthor("Author Test" + number);
        bookVO.setPrice(100.0);
        return bookVO;
    }

}
