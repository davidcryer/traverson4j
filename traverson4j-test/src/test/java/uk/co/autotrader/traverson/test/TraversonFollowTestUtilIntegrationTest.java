package uk.co.autotrader.traverson.test;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.autotrader.traverson.Traverson;
import uk.co.autotrader.traverson.http.ApacheHttpTraversonClientAdapter;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TraversonFollowTestUtilIntegrationTest {
    private static final WireMockServer wireMockServer = new WireMockServer(8089);
    private static final Traverson traverson = new Traverson(new ApacheHttpTraversonClientAdapter());
    private final TraversonFollowTestUtil traversonFollowTestUtil = new TraversonFollowTestUtil(wireMockServer);

    @BeforeClass
    public static void beforeClass() {
        wireMockServer.start();
    }

    @AfterClass
    public static void afterClass() {
        wireMockServer.stop();
    }

    @Before
    public void setUp() {
        wireMockServer.resetAll();
    }

    @Test
    public void follow_single_linksRel() {
        traversonFollowTestUtil.follow("one");
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
        traversonFollowTestUtil.follow("one");
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
        traversonFollowTestUtil.follow("array[prop:one]");
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
        traversonFollowTestUtil.follow("array[prop:one]");
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
        traversonFollowTestUtil.follow("array[0]");
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
        traversonFollowTestUtil.follow("array[0]");
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
    public void follow_multiple() {
        traversonFollowTestUtil.follow("one", "two", "three");
        wireMockServer.stubFor(get("/3").willReturn(ok("success")));

        var response = traverson.from("http://localhost:8089/")
                .jsonHal()
                .follow("one", "two", "three")
                .get(String.class);

        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.getResource()).isEqualTo("success");

        wireMockServer.verify(getRequestedFor(urlEqualTo("/")));
        wireMockServer.verify(getRequestedFor(urlEqualTo("/1")));
        wireMockServer.verify(getRequestedFor(urlEqualTo("/2")));
        wireMockServer.verify(getRequestedFor(urlEqualTo("/3")));
    }
}