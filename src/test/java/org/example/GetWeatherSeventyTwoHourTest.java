package org.example;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class GetWeatherSeventyTwoHourTest extends AbstractTest{


    private static final Logger logger = LoggerFactory.getLogger(GetWeatherSeventyTwoHourTest.class);


    @Test
    void test401ResponseCode() throws URISyntaxException, IOException {

        logger.info("Тест 401 запущен");

        logger.debug("Формирование мока для сервиса GetWeatherSeventyTwoHourTest");

        stubFor(WireMock.get(urlPathEqualTo("/forecasts/v1/hourly/72hour/28580"))
                .withQueryParam("apikey", equalTo( "72"))
                .willReturn(aResponse().withStatus(401).withBody("Error")));

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(getBaseUrl()+"/forecasts/v1/hourly/72hour/28580");
        URI uri = new URIBuilder(get.getURI())
                .addParameter("apikey", "72")
                .build();
        get.setURI(uri);

        logger.debug("Макирование для теста test401ResponseCode завершено");

        HttpResponse response = client.execute(get);

        verify(getRequestedFor(urlPathEqualTo("/forecasts/v1/hourly/72hour/28580")));
        Assertions.assertEquals(401, response.getStatusLine().getStatusCode());



    }
}
