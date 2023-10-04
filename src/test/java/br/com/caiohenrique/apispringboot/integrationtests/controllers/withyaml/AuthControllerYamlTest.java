package br.com.caiohenrique.apispringboot.integrationtests.controllers.withyaml;

import br.com.caiohenrique.apispringboot.configs.TestConfigs;
import br.com.caiohenrique.apispringboot.integrationtests.controllers.withyaml.mapper.YamlMapper;
import br.com.caiohenrique.apispringboot.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.caiohenrique.apispringboot.integrationtests.vo.authorization.AccountCredentialsVO;
import br.com.caiohenrique.apispringboot.integrationtests.vo.authorization.TokenVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
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

import static br.com.caiohenrique.apispringboot.configs.TestConfigs.SERVER_PORT;
import static br.com.caiohenrique.util.MediaType.APPLICATION_YML;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AuthControllerYamlTest extends AbstractIntegrationTest {

    private static TokenVO tokenVO;
    private static YamlMapper yamlMapper;

    @BeforeAll
    public static void setUp() {
        yamlMapper = new YamlMapper();
    }

    @Test
    @Order(1)
    public void testSignIn() throws JsonProcessingException, JsonMappingException {
        AccountCredentialsVO userDefault = new AccountCredentialsVO("caio", "admin123");

        RequestSpecification specification = new RequestSpecBuilder()
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        tokenVO =
                given()
                        .spec(specification)
                        .config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig()
                                .encodeContentTypeAs(APPLICATION_YML, ContentType.TEXT)))
                        .accept(APPLICATION_YML)
                        .basePath("/auth/signin")
                        .port(SERVER_PORT)
                        .contentType(APPLICATION_YML)
                        .body(userDefault, yamlMapper)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .as(TokenVO.class, yamlMapper);

        assertNotNull(tokenVO.getAccessToken());
        assertNotNull(tokenVO.getRefreshToken());
    }

    @Test
    @Order(2)
    public void testRefreshToken() throws JsonMappingException, JsonProcessingException {

        RequestSpecification specification = new RequestSpecBuilder()
                .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

         var tokenVOWithRefresh =
                given()
                        .spec(specification)
                        .config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig()
                                .encodeContentTypeAs(APPLICATION_YML, ContentType.TEXT)))
                        .accept(APPLICATION_YML)
                        .basePath("/auth/refresh")
                        .port(SERVER_PORT)
                        .contentType(APPLICATION_YML)
                        .pathParam("username", tokenVO.getUsername())
                        .header(TestConfigs.HEADER_PARAMS_AUTHORIZATION, "Bearer "+tokenVO.getRefreshToken())
                        .when()
                        .put("{username}")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .as(TokenVO.class, yamlMapper);

        assertNotNull(tokenVOWithRefresh.getAccessToken());
        assertNotNull(tokenVOWithRefresh.getRefreshToken());
    }



}
