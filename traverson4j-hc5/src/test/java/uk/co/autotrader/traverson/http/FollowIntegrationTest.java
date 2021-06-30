package uk.co.autotrader.traverson.http;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.autotrader.traverson.Traverson;

import static org.assertj.core.api.Assertions.assertThat;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class FollowIntegrationTest {

    private static WireMockServer wireMockServer;

    private final Traverson traverson = new Traverson(new ApacheHttpTraversonClientAdapter());

    @BeforeClass
    public static void beforeClass() throws Exception {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        wireMockServer.stop();
        wireMockServer.resetAll();
    }

    @Before
    public void setUp() throws Exception {
        wireMockServer.resetAll();
    }

    @Test
    public void follow_single_linksRel() {
        wireMockServer.stubFor(get("/").willReturn(okJson("{\"_links\":{\"one\":{\"href\":\"http://localhost:8089/1\"}}}")
                .withHeader("Content-Type", "application/hal+json")));
        wireMockServer.stubFor(get("/1").willReturn(ok("success")));

        var response = traverson.from("http://localhost:8089/")
                .jsonHal()
                .follow("one")
                .get(String.class);

        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.getResource()).isEqualTo("success");

        wireMockServer.verify(getRequestedFor(urlEqualTo("/")));
        wireMockServer.verify(getRequestedFor(urlEqualTo("/1")));
    }

    @Test
    public void follow_single_embeddedArrayName() {
        wireMockServer.stubFor(get("/").willReturn(okJson("{\"_embedded\":{\"array\":[{\"name\":\"one\",\"_links\":{\"self\":{\"href\":\"http://localhost:8089/1\"}}}]}}")
                .withHeader("Content-Type", "application/hal+json")));
        wireMockServer.stubFor(get("/1").willReturn(ok("success")));

        var response = traverson.from("http://localhost:8089/")
                .jsonHal()
                .follow("one")
                .get(String.class);

        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.getResource()).isEqualTo("success");

        wireMockServer.verify(getRequestedFor(urlEqualTo("/")));
        wireMockServer.verify(getRequestedFor(urlEqualTo("/1")));
    }

    @Test
    public void follow_single_relByArrayProperty_embedded() {
        wireMockServer.stubFor(get("/").willReturn(okJson("{\"_embedded\":{\"array\":[{\"prop\":\"one\",\"_links\":{\"self\":{\"href\":\"http://localhost:8089/1\"}}}]},\"_links\":{\"self\":{\"href\":\"http://localhost:8089/\"}}}")
                .withHeader("Content-Type", "application/hal+json")));
        wireMockServer.stubFor(get("/1").willReturn(ok("success")));

        var response = traverson.from("http://localhost:8089/")
                .jsonHal()
                .follow("array[prop:one]")
                .get(String.class);

        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.getResource()).isEqualTo("success");

        wireMockServer.verify(getRequestedFor(urlEqualTo("/")));
        wireMockServer.verify(getRequestedFor(urlEqualTo("/1")));
    }

    @Test
    public void follow_single_relByArrayProperty_links() {
        wireMockServer.stubFor(get("/").willReturn(okJson("{\"_links\":{\"array\":[{\"prop\":\"one\",\"href\":\"http://localhost:8089/1\"}]}}")
                .withHeader("Content-Type", "application/hal+json")));
        wireMockServer.stubFor(get("/1").willReturn(ok("success")));

        var response = traverson.from("http://localhost:8089/")
                .jsonHal()
                .follow("array[prop:one]")
                .get(String.class);

        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.getResource()).isEqualTo("success");

        wireMockServer.verify(getRequestedFor(urlEqualTo("/")));
        wireMockServer.verify(getRequestedFor(urlEqualTo("/1")));
    }

    @Test
    public void follow_single_relByArrayIndex_embedded() {
        wireMockServer.stubFor(get("/").willReturn(okJson("{\"_embedded\":{\"array\":[{\"_links\":{\"self\":{\"href\":\"http://localhost:8089/1\"}}},{\"_links\":{\"self\":{\"href\":\"http://localhost:8089/2\"}}}]},\"_links\":{\"self\":{\"href\":\"http://localhost:8089/\"}}}")
                .withHeader("Content-Type", "application/hal+json")));
        wireMockServer.stubFor(get("/1").willReturn(ok("success")));

        var response = traverson.from("http://localhost:8089/")
                .jsonHal()
                .follow("array[0]")
                .get(String.class);

        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.getResource()).isEqualTo("success");

        wireMockServer.verify(getRequestedFor(urlEqualTo("/")));
        wireMockServer.verify(getRequestedFor(urlEqualTo("/1")));
    }

    @Test
    public void follow_single_relByArrayIndex_links() {
        wireMockServer.stubFor(get("/").willReturn(okJson("{\"_links\":{\"array\":[{\"href\":\"http://localhost:8089/1\"},{\"href\":\"http://localhost:8089/2\"}]}}")
                .withHeader("Content-Type", "application/hal+json")));
        wireMockServer.stubFor(get("/1").willReturn(ok("success")));

        var response = traverson.from("http://localhost:8089/")
                .jsonHal()
                .follow("array[0]")
                .get(String.class);

        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.getResource()).isEqualTo("success");

        wireMockServer.verify(getRequestedFor(urlEqualTo("/")));
        wireMockServer.verify(getRequestedFor(urlEqualTo("/1")));
    }
}
