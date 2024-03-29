package org.example;


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
import static org.junit.jupiter.api.Assertions.assertEquals;


public class GetGeopositionSearchTest extends AbstractTest{

        private static final Logger logger = LoggerFactory.getLogger(GetGeopositionSearchTest.class);


        @Test
        void test400ResponseCode() throws URISyntaxException, IOException {

            logger.info("Тест 400 запущен");

            logger.debug("Формирование мока для сервиса GetGeopositionSearchTest");

            stubFor(WireMock.get(urlPathEqualTo("/locations/v1/cities/geoposition/search"))
                    .withQueryParam("apikey", equalTo( "Minsk"))
                    .willReturn(aResponse().withStatus(400).withBody("Error")));

            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet get = new HttpGet(getBaseUrl()+"/locations/v1/cities/geoposition/search");
            URI uri = new URIBuilder(get.getURI())
                    .addParameter("apikey", "Minsk")
                    .build();
            get.setURI(uri);

            logger.debug("Макирование для теста test400ResponseCode завершено");

            HttpResponse response = client.execute(get);

            verify(getRequestedFor(urlPathEqualTo("/locations/v1/cities/geoposition/search")));
            Assertions.assertEquals(400, response.getStatusLine().getStatusCode());



        }
    }