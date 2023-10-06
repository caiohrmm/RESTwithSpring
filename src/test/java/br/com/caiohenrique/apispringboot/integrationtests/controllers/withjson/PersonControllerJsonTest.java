package br.com.caiohenrique.apispringboot.integrationtests.controllers.withjson;

import br.com.caiohenrique.apispringboot.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.caiohenrique.apispringboot.integrationtests.vo.authorization.AccountCredentialsVO;
import br.com.caiohenrique.apispringboot.integrationtests.vo.authorization.TokenVO;
import br.com.caiohenrique.apispringboot.integrationtests.vo.entities.PersonVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.DeserializationFeature;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import static br.com.caiohenrique.apispringboot.configs.TestConfigs.*;
import static br.com.caiohenrique.util.MediaType.APPLICATION_JSON;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

// Como se trata de um teste de integração, preciso garantir uma ordem para as coisas serem feitas
// primeiro gero um VO para depois fazer o update do mesmo ou procurá-lo


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PersonControllerJsonTest extends AbstractIntegrationTest {

    // Criar um setup que será executado antes de todos os testes.
    private static RequestSpecification specification;
    private static RequestSpecification specificationWithoutToken;
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
    @Order(0)
    public void authorization() throws IOException {
        AccountCredentialsVO userDefault = new AccountCredentialsVO("caio", "admin123");

        var accessToken =
                given()
                        .basePath("/auth/signin")
                        .port(SERVER_PORT)
                        .contentType(APPLICATION_JSON)
                        .body(userDefault)
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
                .setBasePath("/persons/v1")
                .setPort(SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        specificationWithoutToken = new RequestSpecBuilder()
                .addHeader(HEADER_PARAMS_AUTHORIZATION, "Bearer ")
                .setBasePath("/persons/v1")
                .setPort(SERVER_PORT)
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();


    }

    @Test
    @Order(1)
    public void testCreate() throws IOException {
        mockPerson();

        // Salvo o conteudo da página em uma variavel
        var content =
                given().spec(specification)
                        .header(HEADER_PARAMS_ORIGIN, ORIGIN_CHRM)
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

        assertEquals("Pedro", persistedPerson.getFirstName());
        assertEquals("Male", persistedPerson.getGender());
        assertEquals("Maringá", persistedPerson.getAddress());
        assertEquals("Pastore", persistedPerson.getLastName());
    }

    @Test
    @Order(2)
    public void testUpdate() throws IOException {
        personVO.setLastName("Arthur");

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
        assertEquals(persistedPerson.getId(), personVO.getId());

        assertEquals("Pedro", persistedPerson.getFirstName());
        assertEquals("Male", persistedPerson.getGender());
        assertEquals("Maringá", persistedPerson.getAddress());
        assertEquals("Arthur", persistedPerson.getLastName());

    }


    @Test
    @Order(3)
    public void testFindById() throws IOException {

        // Salvo o conteudo da página em uma variavel
        var content =
                given().spec(specification)
                        .contentType(APPLICATION_JSON)
                        .pathParam("id", personVO.getId())
                        .when()
                        .get("{id}")
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
        assertEquals(persistedPerson.getId(), personVO.getId());

        assertEquals("Pedro", persistedPerson.getFirstName());
        assertEquals("Male", persistedPerson.getGender());
        assertEquals("Maringá", persistedPerson.getAddress());
        assertEquals("Arthur", persistedPerson.getLastName());

    }

    @Test
    @Order(4)
    public void testDelete() throws IOException {

        // Salvo o conteudo da página em uma variavel
        given()
                .spec(specification)
                .pathParam("id", personVO.getId())
                .when()
                .delete("{id}")
                .then()
                .statusCode(204);
    }

    private void mockPerson() {
        personVO.setFirstName("Pedro");
        personVO.setLastName("Pastore");
        personVO.setAddress("Maringá");
        personVO.setGender("Male");
        personVO.setEnabled(true);
    }

    @Test
    @Order(5)
    public void testFindAll() throws IOException {
        mockPerson();

        // O TestContainers le as migrations e cria o banco de testes de acordo com elas.
        // Content chega em lista.

        // Salvo o conteúdo da página em uma variável
        var content =
                given().spec(specification)
                        .contentType(APPLICATION_JSON)
                        .when()
                        .get()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body().asString();
        List<PersonVO> people = objectMapper.readValue(content, new TypeReference<List<PersonVO>>() {
        });


        // Para transformar o valor criado em Vo e conseguir ler ele.
        PersonVO personOne = people.get(0);
        personVO = personOne;

        assertEquals(personOne.getId(), 1);

        assertEquals("Caio Henrique Rodrigues", personOne.getFirstName());
        assertEquals("Male", personOne.getGender());
        assertEquals("SP", personOne.getAddress());
        assertEquals("Martins", personOne.getLastName());
    }


    @Test
    @Order(6)
    public void testFindAllWithoutToken() throws IOException {
        mockPerson();

        // O TestContainers le as migrations e cria o banco de testes de acordo com elas.
        // Content chega em lista.

        // Salvo o conteúdo da página em uma variável
        given().spec(specificationWithoutToken)
                .contentType(APPLICATION_JSON)
                .when()
                .get()
                .then()
                .statusCode(403);
    }

    @Test
    @Order(7)
    public void testDisablePerson() throws IOException {

        personVO.setEnabled(false);

        // Salvo o conteudo da página em uma variavel
        var content =
                given().spec(specification)
                        .contentType(APPLICATION_JSON)
                        .pathParam("id", personVO.getId())
                        .when()
                        .patch("{id}")
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
        assertNotNull(persistedPerson.getEnabled());
        assertEquals(persistedPerson.getId(), personVO.getId());

        assertEquals("Pedro", persistedPerson.getFirstName());
        assertEquals("Male", persistedPerson.getGender());
        assertEquals("Maringá", persistedPerson.getAddress());
        assertEquals("Arthur", persistedPerson.getLastName());
        assertFalse(persistedPerson.getEnabled());
    }
}
