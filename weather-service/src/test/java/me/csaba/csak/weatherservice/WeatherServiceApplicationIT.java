package me.csaba.csak.weatherservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest
@ActiveProfiles("it")
class WeatherServiceApplicationIT {

    public static final int MOCK_SERVER_PORT = 4545;

    @RegisterExtension
    public static WireMockExtension wireMockRule = WireMockExtension.newInstance()
            .options(wireMockConfig().port(MOCK_SERVER_PORT))
            .build();

    @BeforeAll
    public static void setup() throws JsonProcessingException {
        wireMockRule.stubFor(
                get(urlEqualTo("/events")).willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(new ObjectMapper().writeValueAsBytes(Collections.emptyList()))
                        .withStatus(200)));
    }

    @Test
    void contextLoads() {
    }

}
