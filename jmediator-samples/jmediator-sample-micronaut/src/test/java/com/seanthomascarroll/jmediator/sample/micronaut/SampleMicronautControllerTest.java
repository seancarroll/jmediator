package com.seanthomascarroll.jmediator.sample.micronaut;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
class SampleMicronautControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

//    private static EmbeddedServer server;
//    private static HttpClient client;
//
//    @BeforeAll
//    static void setupServer() {
//        server = ApplicationContext.run(EmbeddedServer.class);
//        client = server
//            .getApplicationContext()
//            .createBean(HttpClient.class, server.getURL());
//    }
//
//    @AfterAll
//    static void stopServer() {
//        if (server != null) {
//            server.stop();
//        }
//        if (client != null) {
//            client.stop();
//        }
//    }


    @Test
    void testHello() {
        String rsp = client.toBlocking().retrieve(HttpRequest.GET("/hello/sean"));
        assertEquals("Hello sean", rsp);
    }

}
