package br.com.caiohenrique.apispringboot.integrationtests.controllers.withyaml;

import br.com.caiohenrique.apispringboot.integrationtests.controllers.withyaml.mapper.YamlMapper;
import br.com.caiohenrique.apispringboot.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.caiohenrique.apispringboot.integrationtests.vo.authorization.AccountCredentialsVO;
import br.com.caiohenrique.apispringboot.integrationtests.vo.authorization.TokenVO;
import br.com.caiohenrique.apispringboot.integrationtests.vo.entities.BookVO;
import br.com.caiohenrique.apispringboot.integrationtests.vo.pagedmodels.PagedModelBook;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static br.com.caiohenrique.apispringboot.configs.TestConfigs.HEADER_PARAMS_AUTHORIZATION;
import static br.com.caiohenrique.apispringboot.configs.TestConfigs.SERVER_PORT;
import static br.com.caiohenrique.util.MediaType.APPLICATION_XML;
import static br.com.caiohenrique.util.MediaType.APPLICATION_YML;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

// Como se trata de um teste de integração, preciso garantir uma ordem para as coisas serem feitas
// primeiro gero um VO para depois fazer o update do mesmo ou procurá-lo


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class BookControllerYamlTest extends AbstractIntegrationTest {

    // Criar um setup que será executado antes de todos os testes.
    private static RequestSpecification specification;
    private static RequestSpecification specificationWithoutToken;
    private static YamlMapper objectMapper;
    private static BookVO bookVO;

    @BeforeAll
    public static void beforeAll() {
        objectMapper = new YamlMapper();
        // Recebo o JSON com HATEOAS, sem essa propriedade ele nao consegue ignorar o erro.
        bookVO = new BookVO();
        // Aqui o contexto do spring ainda não está em execucao, por isso,
        // Nao posso definir as specifications aqui
    }

    @Test
    @Order(0)
    public void authorization() throws IOException {
        AccountCredentialsVO userDefault = new AccountCredentialsVO("caio", "admin123");

        var accessToken =
                given()
                        .basePath("/auth/signin")
                        .port(SERVER_PORT)
                        .contentType(APPLICATION_YML)
                        .config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig()
                                .encodeContentTypeAs(APPLICATION_YML, ContentType.TEXT)))
                        .body(userDefault, objectMapper)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .as(TokenVO.class)
                        .getAccessToken();

        specification = new RequestSpecBuilder()
                .addHeader(HEADER_PARAMS_AUTHORIZATION, "Bearer " + accessToken)
                .setBasePath("/books/v1")
                .setPort(SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        specificationWithoutToken = new RequestSpecBuilder()
                .addHeader(HEADER_PARAMS_AUTHORIZATION, "Bearer ")
                .setBasePath("/books/v1")
                .setPort(SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();


    }

    @Test
    @Order(1)
    public void testCreate() throws IOException, ParseException {
        mockBook();

        var persistedBook =
                given().spec(specification)
                        .contentType(APPLICATION_YML)
                        .accept(APPLICATION_YML)
                        .config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig()
                                .encodeContentTypeAs(APPLICATION_YML, ContentType.TEXT)))
                        .body(bookVO, objectMapper)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body().as(BookVO.class, objectMapper);

        bookVO = persistedBook;

        assertNotNull(persistedBook);
        assertNotNull(persistedBook.getKey());
        assertNotNull(persistedBook.getAuthor());
        assertNotNull(persistedBook.getLaunchDate());
        assertNotNull(persistedBook.getTitle());
        assertNotNull(persistedBook.getPrice());
        assertTrue(persistedBook.getKey() > 0);

        assertEquals("Machado de Assis", persistedBook.getAuthor());
        assertEquals("Memórias Póstumas de Brás Cubas", persistedBook.getTitle());
        assertEquals(100.00, persistedBook.getPrice());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String date = "29/11/1881";
        assertEquals(sdf.parse(date), persistedBook.getLaunchDate());
    }

    @Test
    @Order(2)
    public void testUpdate() throws IOException, ParseException {
        bookVO.setAuthor("Graciliano Ramos");

        // Salvo o conteudo da página em uma variavel
        var persistedBook =
                given().spec(specification)
                        .contentType(APPLICATION_YML)
                        .accept(APPLICATION_YML)
                        .config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig()
                                .encodeContentTypeAs(APPLICATION_YML, ContentType.TEXT)))
                        .body(bookVO, objectMapper)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body().as(BookVO.class, objectMapper);

        // Para transformar o valor criado em Vo e conseguir ler ele.
        bookVO = persistedBook;

        assertNotNull(persistedBook);
        assertNotNull(persistedBook.getKey());
        assertNotNull(persistedBook.getAuthor());
        assertNotNull(persistedBook.getLaunchDate());
        assertNotNull(persistedBook.getTitle());
        assertNotNull(persistedBook.getPrice());
        assertEquals(persistedBook.getKey(), bookVO.getKey());

        assertEquals("Graciliano Ramos", persistedBook.getAuthor());
        assertEquals("Memórias Póstumas de Brás Cubas", persistedBook.getTitle());
        assertEquals(100.00, persistedBook.getPrice());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String date = "29/11/1881";
        assertEquals(sdf.parse(date), persistedBook.getLaunchDate());
    }


    @Test
    @Order(3)
    public void testFindById() throws IOException, ParseException {

        // Salvo o conteudo da página em uma variavel
        var persistedBook =
                given().spec(specification)
                        .contentType(APPLICATION_YML)
                        .accept(APPLICATION_YML)
                        .config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig()
                                .encodeContentTypeAs(APPLICATION_YML, ContentType.TEXT)))
                        .pathParam("id", bookVO.getKey())
                        .when()
                        .get("{id}")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body().as(BookVO.class, objectMapper);

        bookVO = persistedBook;

        assertNotNull(persistedBook);
        assertNotNull(persistedBook.getKey());
        assertNotNull(persistedBook.getAuthor());
        assertNotNull(persistedBook.getLaunchDate());
        assertNotNull(persistedBook.getTitle());
        assertNotNull(persistedBook.getPrice());
        assertEquals(persistedBook.getKey(), bookVO.getKey());

        assertEquals("Graciliano Ramos", persistedBook.getAuthor());
        assertEquals("Memórias Póstumas de Brás Cubas", persistedBook.getTitle());
        assertEquals(100.00, persistedBook.getPrice());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String date = "29/11/1881";
        assertEquals(sdf.parse(date), persistedBook.getLaunchDate());

    }

    @Test
    @Order(4)
    public void testDelete() throws IOException {

        // Salvo o conteudo da página em uma variavel
        given()
                .spec(specification)
                .pathParam("id", bookVO.getKey())
                .when()
                .delete("{id}")
                .then()
                .statusCode(204);
    }



    @Test
    @Order(5)
    public void testFindAll() throws IOException, ParseException {

        // O TestContainers le as migrations e cria o banco de testes de acordo com elas.
        // Content chega em lista.

        // Salvo o conteúdo da página em uma variável
        var contentList =
                given().spec(specification)
                        .contentType(APPLICATION_YML)
                        .accept(APPLICATION_YML)
                        .queryParams("page", 1 , "size", 15, "direction", "asc")
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body().as(PagedModelBook.class, objectMapper);

        var library = contentList.getContent();

        // Para transformar o valor criado em Vo e conseguir ler ele.
        BookVO bookOne = library.get(0);


        assertEquals(bookOne.getKey(), 1011);
        assertEquals("Afton Slader", bookOne.getAuthor());
        assertEquals("Good Year, A", bookOne.getTitle());
        assertEquals(369.05, bookOne.getPrice());
    }

    @Test
    @Order(6)
    public void testFindBookByTitle() throws IOException, ParseException {

        // O TestContainers le as migrations e cria o banco de testes de acordo com elas.
        // Content chega em lista.

        String title = "se";

        // Salvo o conteúdo da página em uma variável
        var contentList =
                given().spec(specification)
                        .contentType(APPLICATION_YML)
                        .accept(APPLICATION_YML)
                        .queryParams("page", 1 , "size", 15, "direction", "desc")
                        .pathParam("title", title)
                        .when()
                        .get("/findBookByTitle/{title}")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body().as(PagedModelBook.class, objectMapper);

        var library = contentList.getContent();

        // Para transformar o valor criado em Vo e conseguir ler ele.
        BookVO bookOne = library.get(0);


        assertEquals(bookOne.getKey(), 544);
        assertEquals("Rennie Melin", bookOne.getAuthor());
        assertEquals("I Never Promised You a Rose Garden", bookOne.getTitle());
        assertEquals(377.0, bookOne.getPrice());
        assertNotNull(bookOne.getLaunchDate());
    }

    @Test
    @Order(7)
    public void testFindAllWithoutToken() throws IOException {
        // O TestContainers le as migrations e cria o banco de testes de acordo com elas.
        // Content chega em lista.

        // Salvo o conteúdo da página em uma variável
        given().spec(specificationWithoutToken)
                .contentType(APPLICATION_XML)
                .accept(APPLICATION_XML)
                .when()
                .get()
                .then()
                .statusCode(403);
    }

    private void mockBook() throws ParseException {
        bookVO.setAuthor("Machado de Assis");
        bookVO.setTitle("Memórias Póstumas de Brás Cubas");
        bookVO.setPrice(100.00);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String date = "29/11/1881";
        bookVO.setLaunchDate(sdf.parse(date));
    }

}
