package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.example.location.Location;
import org.example.weather.Weather;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class GetWeatherFiveDaysTest extends AbstractTest {

    private static final Logger logger
            = LoggerFactory.getLogger(GetWeatherFiveDaysTest.class);

    @Test
    void test200ResponseCode() throws IOException, URISyntaxException {
        logger.info("Тест 200 запущен");
        ObjectMapper objectMapper = new ObjectMapper();
        Weather bodyOk = new Weather();
        bodyOk.setKey("Ok");

        Weather bodyError = new Weather();
        bodyError.setKey("Error");

        stubFor(WireMock.get(urlPathEqualTo("/forecasts/v1/daily/5day/28580"))
                .withQueryParam("q", equalTo("5"))
                .willReturn(aResponse().withStatus(200).withBody(objectMapper.writeValueAsString(bodyOk))));

        stubFor(WireMock.get(urlPathEqualTo("/forecasts/v1/daily/5day/28580"))
                .withQueryParam("q", equalTo("error"))
                .willReturn(aResponse().withStatus(200).withBody(objectMapper.writeValueAsString(bodyError))));

        logger.debug("Макирование для теста test200ResponseCode завершено");

        //when
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(getBaseUrl() + "/forecasts/v1/daily/5day/28580");

        URI uri = new URIBuilder(get.getURI())
                .addParameter("q", "5")
                .build();
        get.setURI(uri);

        HttpResponse responseOk = client.execute(get);

        URI uriError = new URIBuilder(get.getURI())
                .addParameter("q", "error")
                .build();
        get.setURI(uriError);

        HttpResponse responseError = client.execute(get);


        verify(2, getRequestedFor(urlPathEqualTo("/forecasts/v1/daily/5day/28580")));
        Assertions.assertEquals(200, responseOk.getStatusLine().getStatusCode());
        Assertions.assertEquals(200, responseError.getStatusLine().getStatusCode());

        Weather WeatherOk = objectMapper.readValue(responseOk.getEntity().getContent(), Weather.class);
        Weather WeatherError = objectMapper.readValue(responseError.getEntity().getContent(), Weather.class);

        Assertions.assertEquals("Ok", WeatherOk.getKey());
        Assertions.assertEquals("Error", WeatherError.getKey());
    }
}
