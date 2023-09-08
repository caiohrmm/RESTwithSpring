package br.com.caiohenrique.apispringboot.integrationtests.controllers.withjson;

import br.com.caiohenrique.apispringboot.configs.TestConfigs;
import br.com.caiohenrique.apispringboot.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.caiohenrique.apispringboot.integrationtests.vo.PersonVO;
import static br.com.caiohenrique.util.MediaType.*;
import static br.com.caiohenrique.apispringboot.configs.TestConfigs.*;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.DeserializationFeature;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

// Como se trata de um teste de integração, preciso garantir uma ordem para as coisas serem feitas
// primeiro gero um  VO para depois fazer o update do mesmo ou procurá-lo


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PersonControllerJsonTest extends AbstractIntegrationTest {

    // Criar um setup que será executado antes de todos os testes.
    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;
    private static PersonVO personVO;

    @BeforeAll
    public static void beforeAll() {
        objectMapper = new ObjectMapper();
        // Recebo o JSON com HATEOAS, sem essa propriedade ele nao consegue ignorar o erro.
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        personVO = new PersonVO();
        // Aqui o contexto do spring ainda não está em execucao, por isso,
        // Nao posso definir as specifications aqui
    }

    @Test
    @Order(1)
    public void testCreate() throws IOException {
        mockPerson();

        // Como se eu estivesse configurando o Postman
        specification = new RequestSpecBuilder()
                .addHeader(HEADER_PARAMS_ORIGIN, ORIGIN_CHRM)
                .setBasePath("/persons/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        // Salvo o conteudo da página em uma variavel
        var content =
                given().spec(specification)
                        .contentType(APPLICATION_JSON)
                        .body(personVO)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body().asString();

        // Para transformar o valor criado em Vo e conseguir ler ele.
        PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
        personVO = persistedPerson;

        assertNotNull(persistedPerson);
        assertNotNull(persistedPerson.getId());
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getGender());
        assertNotNull(persistedPerson.getAddress());
        assertNotNull(persistedPerson.getLastName());
        assertTrue(persistedPerson.getId() > 0);

        assertEquals("Caio",persistedPerson.getFirstName());
        assertEquals("Male",persistedPerson.getGender());
        assertEquals("London",persistedPerson.getAddress());
        assertEquals("Henrique",persistedPerson.getLastName());

    }
    @Test
    @Order(2)
    public void testCreateWithWrongOrigin() throws IOException {
        mockPerson();

        // Como se eu estivesse configurando o Postman
        specification = new RequestSpecBuilder()
                .addHeader(HEADER_PARAMS_ORIGIN, ORIGIN_WRONG)
                .setBasePath("/persons/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        // Salvo o conteudo da página em uma variavel
        var content =
                given().spec(specification)
                        .contentType(APPLICATION_JSON)
                        .body(personVO)
                        .when()
                        .post()
                        .then()
                        .statusCode(403)
                        .extract()
                        .body().asString();
       // Apenas deve me retornar o content com uma string de erro de CORS.

        assertNotNull(content);
        assertTrue(content.contains("Invalid CORS request"));

    }

    private void mockPerson() {
        personVO.setFirstName("Caio");
        personVO.setLastName("Henrique");
        personVO.setAddress("London");
        personVO.setGender("Male");

    }

    @Test
    @Order(1)
    public void testFindById() throws IOException {

        // Como se eu estivesse configurando o Postman
        specification = new RequestSpecBuilder()
                .addHeader(HEADER_PARAMS_ORIGIN, ORIGIN_CHRM)
                .setBasePath("/persons/v1")
                .setPort(TestConfigs.SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        // Salvo o conteudo da página em uma variavel
        var content =
                given().spec(specification)
                        .contentType(APPLICATION_JSON)
                        .pathParam("id", personVO.getId())
                        .when()
                        .get("")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body().asString();

        // Para transformar o valor criado em Vo e conseguir ler ele.
        PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
        personVO = persistedPerson;

        assertNotNull(persistedPerson);
        assertNotNull(persistedPerson.getId());
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getGender());
        assertNotNull(persistedPerson.getAddress());
        assertNotNull(persistedPerson.getLastName());
        assertTrue(persistedPerson.getId() > 0);

        assertEquals("Caio",persistedPerson.getFirstName());
        assertEquals("Male",persistedPerson.getGender());
        assertEquals("London",persistedPerson.getAddress());
        assertEquals("Henrique",persistedPerson.getLastName());

    }


}
