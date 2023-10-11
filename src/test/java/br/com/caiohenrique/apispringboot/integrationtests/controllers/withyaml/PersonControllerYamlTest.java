package br.com.caiohenrique.apispringboot.integrationtests.controllers.withyaml;

import br.com.caiohenrique.apispringboot.integrationtests.controllers.withyaml.mapper.YamlMapper;
import br.com.caiohenrique.apispringboot.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.caiohenrique.apispringboot.integrationtests.vo.authorization.AccountCredentialsVO;
import br.com.caiohenrique.apispringboot.integrationtests.vo.authorization.TokenVO;
import br.com.caiohenrique.apispringboot.integrationtests.vo.entities.PersonVO;
import br.com.caiohenrique.apispringboot.integrationtests.vo.pagedmodels.PagedModelPerson;
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
public class PersonControllerYamlTest extends AbstractIntegrationTest {

    // Criar um setup que será executado antes de todos os testes.
    private static RequestSpecification specification;
    private static RequestSpecification specificationWithoutToken;
    private static YamlMapper objectMapper;
    private static PersonVO personVO;

    @BeforeAll
    public static void beforeAll() {
        objectMapper = new YamlMapper();
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
        var persistedPerson =
                given().spec(specification)
                        .contentType(APPLICATION_YML)
                        .accept(APPLICATION_YML)
                        .config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig()
                                .encodeContentTypeAs(APPLICATION_YML, ContentType.TEXT)))
                        .body(personVO, objectMapper)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body().as(PersonVO.class, objectMapper);

        // Para transformar o valor criado em Vo e conseguir ler ele.
        personVO = persistedPerson;

        assertNotNull(persistedPerson);
        assertNotNull(persistedPerson.getId());
        assertNotNull(persistedPerson.getFirstName());
        assertNotNull(persistedPerson.getGender());
        assertNotNull(persistedPerson.getAddress());
        assertNotNull(persistedPerson.getLastName());
        assertNotNull(persistedPerson.getEnabled());
        assertTrue(persistedPerson.getId() > 0);

        assertEquals("Pedro", persistedPerson.getFirstName());
        assertEquals("Male", persistedPerson.getGender());
        assertEquals("Maringá", persistedPerson.getAddress());
        assertEquals("Pastore", persistedPerson.getLastName());
        assertTrue(persistedPerson.getEnabled());
    }

    @Test
    @Order(2)
    public void testUpdate() throws IOException {
        personVO.setLastName("Arthur");

        // Salvo o conteudo da página em uma variavel
        var persistedPerson =
                given().spec(specification)
                        .config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig()
                                .encodeContentTypeAs(APPLICATION_YML, ContentType.TEXT)))
                        .contentType(APPLICATION_YML)
                        .accept(APPLICATION_YML)
                        .body(personVO, objectMapper)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body().as(PersonVO.class, objectMapper);

        // Para transformar o valor criado em Vo e conseguir ler ele.
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
        assertTrue(persistedPerson.getEnabled());

    }


    @Test
    @Order(3)
    public void testDisablePersonById() throws IOException {

        // Salvo o conteudo da página em uma variavel
        var persistedPerson =
                given().spec(specification)
                        .contentType(APPLICATION_YML)
                        .accept(APPLICATION_YML)
                        .config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig()
                                .encodeContentTypeAs(APPLICATION_YML, ContentType.TEXT)))
                        .pathParam("id", personVO.getId())
                        .when()
                        .patch("disableperson/{id}")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body().as(PersonVO.class, objectMapper);

        // Para transformar o valor criado em Vo e conseguir ler ele.
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

    @Test
    @Order(4)
    public void testFindById() throws IOException {

        // Salvo o conteudo da página em uma variavel
        var persistedPerson =
                given().spec(specification)
                        .contentType(APPLICATION_YML)
                        .accept(APPLICATION_YML)
                        .config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig()
                                .encodeContentTypeAs(APPLICATION_YML, ContentType.TEXT)))
                        .pathParam("id", personVO.getId())
                        .when()
                        .get("{id}")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body().as(PersonVO.class, objectMapper);

        // Para transformar o valor criado em Vo e conseguir ler ele.
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

    @Test
    @Order(5)
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



    @Test
    @Order(6)
    public void testFindAll() throws IOException {

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
                        .body().as(PagedModelPerson.class, objectMapper);

        // Para transformar o valor criado em Vo e conseguir ler ele.
        List<PersonVO> people = contentList.getContent();


        // Para transformar o valor criado em Vo e conseguir ler ele.
        PersonVO personOne = people.get(0);
        personVO = personOne;

        assertEquals(personOne.getId(), 303);

        assertEquals("Aili", personOne.getFirstName());
        assertEquals("Tuxwell", personOne.getLastName());
        assertEquals("7803 Darwin Pass", personOne.getAddress());
        assertEquals("Female", personOne.getGender());
        assertTrue(personOne.getEnabled());
    }

    @Test
    @Order(7)
    public void testFindAllWithoutToken() throws IOException {
        mockPerson();

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

    private void mockPerson() {
        personVO.setFirstName("Pedro");
        personVO.setLastName("Pastore");
        personVO.setAddress("Maringá");
        personVO.setGender("Male");
        personVO.setEnabled(true);
    }


}
