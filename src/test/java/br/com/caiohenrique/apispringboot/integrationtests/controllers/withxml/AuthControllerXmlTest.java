package br.com.caiohenrique.apispringboot.integrationtests.controllers.withxml;

import br.com.caiohenrique.apispringboot.configs.TestConfigs;
import br.com.caiohenrique.apispringboot.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.caiohenrique.apispringboot.integrationtests.vo.authorization.AccountCredentialsVO;
import br.com.caiohenrique.apispringboot.integrationtests.vo.authorization.TokenVO;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static br.com.caiohenrique.apispringboot.configs.TestConfigs.SERVER_PORT;
import static br.com.caiohenrique.util.MediaType.APPLICATION_JSON;
import static br.com.caiohenrique.util.MediaType.APPLICATION_XML;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AuthControllerXmlTest extends AbstractIntegrationTest {

    private static TokenVO tokenVO;

    @Test
    @Order(1)
    public void testSignIn() throws IOException {
        AccountCredentialsVO userDefault = new AccountCredentialsVO("caio", "admin123");

        tokenVO =
                given()
                        .basePath("/auth/signin")
                        .port(SERVER_PORT)
                        .contentType(APPLICATION_XML)
                        .accept(APPLICATION_XML)
                        .body(userDefault)
                        .when()
                        .post()
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .as(TokenVO.class);

        assertNotNull(tokenVO.getAccessToken());
        assertNotNull(tokenVO.getRefreshToken());
    }

    @Test
    @Order(2)
    public void testRefreshToken() throws IOException {

         var tokenVOWithRefresh =
                given()
                        .basePath("/auth/refresh")
                        .port(SERVER_PORT)
                        .accept(APPLICATION_XML)
                        .contentType(APPLICATION_XML)
                        .pathParam("username", tokenVO.getUsername())
                        .header(TestConfigs.HEADER_PARAMS_AUTHORIZATION, "Bearer "+tokenVO.getRefreshToken())
                        .when()
                        .put("{username}")
                        .then()
                        .statusCode(200)
                        .extract()
                        .body()
                        .as(TokenVO.class);

        assertNotNull(tokenVOWithRefresh.getAccessToken());
        assertNotNull(tokenVOWithRefresh.getRefreshToken());
    }



}
