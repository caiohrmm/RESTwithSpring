package br.com.caiohenrique.config;

import br.com.caiohenrique.serialization.converter.YamlJackson2HttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final MediaType MEDIA_TYPE_APPLICATION_YML = MediaType.valueOf("application/x-yaml");

    /*
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        // Irei implementar o XML via query params - http://localhost:8080/persons/v1?mediaType=xml
                configurer.favorParameter(true)
                .parameterName("mediaType")
                .ignoreAcceptHeader(true)
                .useRegisteredExtensionsOnly(false)
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("json", MediaType.APPLICATION_JSON)
                .mediaType("xml", MediaType.APPLICATION_XML);
    }
     */

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        // Irei implementar o XML via header - http://localhost:8080/persons/v1?mediaType=xml
                configurer.favorParameter(false)
                .ignoreAcceptHeader(false)
                .useRegisteredExtensionsOnly(false)
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("json", MediaType.APPLICATION_JSON)
                .mediaType("xml", MediaType.APPLICATION_XML).mediaType("x-yaml",MEDIA_TYPE_APPLICATION_YML);
    }

    // Adicionando meu messageConverter aqui
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new YamlJackson2HttpMessageConverter());
    }
}
