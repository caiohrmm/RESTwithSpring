package br.com.caiohenrique.apispringboot.integrationtests.swagger;

import br.com.caiohenrique.apispringboot.configs.TestConfigs;
import br.com.caiohenrique.apispringboot.integrationtests.testcontainers.AbstractIntegrationTest;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SwaggerIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void shouldDisplaySwaggerUiPage() {
        // Salvo o conteudo da página em uma variavel
        // Se conter Swagger UI é porque a página carregou.
        var content =
        given().basePath("/swagger-ui/index.html")
                .port(TestConfigs.SERVER_PORT)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .body().asString();

        assertTrue(content.contains("Swagger UI"));

    }

}
