package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.example.location.Location;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.example.AbstractTest.getBaseUrl;

public class GetRegionTest extends AbstractTest {

    private static final Logger logger
            = LoggerFactory.getLogger(GetRegionTest.class);

    @Test
    void test200ResponseCode() throws IOException, URISyntaxException {
        logger.info("Тест 200 запущен");
        ObjectMapper objectMapper = new ObjectMapper();
        Location bodyOk = new Location();
        bodyOk.setKey("Ok");

        Location bodyError = new Location();
        bodyError.setKey("Error");

        stubFor(WireMock.get(urlPathEqualTo("/locations/v1/regions"))
                .withQueryParam("q", equalTo("Minsk"))
                .willReturn(aResponse().withStatus(200).withBody(objectMapper.writeValueAsString(bodyOk))));

        stubFor(WireMock.get(urlPathEqualTo("/locations/v1/regions"))
                .withQueryParam("q", equalTo("error"))
                .willReturn(aResponse().withStatus(200).withBody(objectMapper.writeValueAsString(bodyError))));

        logger.debug("Макирование для теста test200ResponseCode завершено");

        //when
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(getBaseUrl()+"/locations/v1/regions");

        URI uri = new URIBuilder(get.getURI())
                .addParameter("q", "Minsk")
                .build();
        get.setURI(uri);

        HttpResponse responseOk = client.execute(get);

        URI uriError = new URIBuilder(get.getURI())
                .addParameter("q", "error")
                .build();
        get.setURI(uriError);

        HttpResponse responseError = client.execute(get);


        verify(2,getRequestedFor(urlPathEqualTo("/locations/v1/regions")));
        Assertions.assertEquals(200, responseOk.getStatusLine().getStatusCode());
        Assertions.assertEquals(200, responseError.getStatusLine().getStatusCode());

        Location LocationOk = objectMapper.readValue(responseOk.getEntity().getContent(), Location.class);
        Location LocationError = objectMapper.readValue(responseError.getEntity().getContent(), Location.class);

        Assertions.assertEquals("Ok", LocationOk.getKey());
        Assertions.assertEquals("Error", LocationError.getKey());
    }
}
