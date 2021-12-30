package uk.co.autotrader.traverson.test;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.autotrader.traverson.Traverson;
import uk.co.autotrader.traverson.http.ApacheHttpTraversonClientAdapter;
import uk.co.autotrader.traverson.test.wiremock.TraversonWiremockUtils;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TraversonFollowTestUtilIntegrationTest {
    private static final WireMockServer wireMockServer = new WireMockServer(8089);
    private static final Traverson traverson = new Traverson(new ApacheHttpTraversonClientAdapter());

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
        var followVerification = TraversonWiremockUtils.follow(wireMockServer, "one");
        wireMockServer.stubFor(get("/resource").willReturn(ok("success")));

        var response = traverson.from("http://localhost:8089/")
                .jsonHal()
                .follow("one")
                .get(String.class);

        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.getResource()).isEqualTo("success");

        followVerification.verifyFollowsCalled();//TODO this
        TraversonWiremockUtils.verifyFollowsCalled(wireMockServer, 1);//TODO or this?
        wireMockServer.verify(getRequestedFor(urlEqualTo("/resource")));
    }

    @Test
    public void follow_single_embeddedArrayName() {
        TraversonWiremockUtils.follow(wireMockServer, "one");
        wireMockServer.stubFor(get("/resource").willReturn(ok("success")));

        var response = traverson.from("http://localhost:8089/")
                .jsonHal()
                .follow("one")
                .get(String.class);

        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.getResource()).isEqualTo("success");

        TraversonWiremockUtils.verifyFollowsCalled(wireMockServer, 1);
        wireMockServer.verify(getRequestedFor(urlEqualTo("/resource")));
    }

    @Test
    public void follow_single_relByArrayProperty_embedded() {
        TraversonWiremockUtils.follow(wireMockServer, "array[prop:one]");
        wireMockServer.stubFor(get("/resource").willReturn(ok("success")));

        var response = traverson.from("http://localhost:8089/")
                .jsonHal()
                .follow("array[prop:one]")
                .get(String.class);

        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.getResource()).isEqualTo("success");

        TraversonWiremockUtils.verifyFollowsCalled(wireMockServer, 1);
        wireMockServer.verify(getRequestedFor(urlEqualTo("/resource")));
    }

    @Test
    public void follow_single_relByArrayProperty_links() {
        TraversonWiremockUtils.follow(wireMockServer, "array[prop:one]");
        wireMockServer.stubFor(get("/resource").willReturn(ok("success")));

        var response = traverson.from("http://localhost:8089/")
                .jsonHal()
                .follow("array[prop:one]")
                .get(String.class);

        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.getResource()).isEqualTo("success");


        TraversonWiremockUtils.verifyFollowsCalled(wireMockServer, 1);
        wireMockServer.verify(getRequestedFor(urlEqualTo("/resource")));
    }

    @Test
    public void follow_single_relByArrayIndex_embedded() {
        TraversonWiremockUtils.follow(wireMockServer, "array[0]");
        wireMockServer.stubFor(get("/resource").willReturn(ok("success")));

        var response = traverson.from("http://localhost:8089/")
                .jsonHal()
                .follow("array[0]")
                .get(String.class);

        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.getResource()).isEqualTo("success");

        TraversonWiremockUtils.verifyFollowsCalled(wireMockServer, 1);
        wireMockServer.verify(getRequestedFor(urlEqualTo("/resource")));
    }

    @Test
    public void follow_single_relByArrayIndex_links() {
        TraversonWiremockUtils.follow(wireMockServer, "array[0]");
        wireMockServer.stubFor(get("/resource").willReturn(ok("success")));

        var response = traverson.from("http://localhost:8089/")
                .jsonHal()
                .follow("array[0]")
                .get(String.class);

        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.getResource()).isEqualTo("success");

        TraversonWiremockUtils.verifyFollowsCalled(wireMockServer, 1);
        wireMockServer.verify(getRequestedFor(urlEqualTo("/resource")));
    }

    @Test
    public void follow_withQueryParam() {
        TraversonWiremockUtils.follow(wireMockServer, "one");
        wireMockServer.stubFor(get("/resource?k=v").willReturn(ok("success")));

        var response = traverson.from("http://localhost:8089/")
                .jsonHal()
                .follow("one")
                .withQueryParam("k", "v")
                .get(String.class);

        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.getResource()).isEqualTo("success");

        TraversonWiremockUtils.verifyFollowsCalled(wireMockServer, 1);
        wireMockServer.verify(getRequestedFor(urlEqualTo("/resource?k=v")));
    }

    @Test
    public void follow_multiple() {
        TraversonWiremockUtils.follow(wireMockServer, "one", "two", "three");
        wireMockServer.stubFor(get("/resource").willReturn(ok("success")));

        var response = traverson.from("http://localhost:8089/")
                .jsonHal()
                .follow("one", "two", "three")
                .get(String.class);

        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.getResource()).isEqualTo("success");

        TraversonWiremockUtils.verifyFollowsCalled(wireMockServer, 3);
        wireMockServer.verify(getRequestedFor(urlEqualTo("/resource")));
    }
}