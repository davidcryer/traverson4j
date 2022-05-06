package uk.co.autotrader.traverson;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.autotrader.traverson.http.ApacheHttpTraversonClientAdapter;
import uk.co.autotrader.traverson.http.SimpleMultipartBody;
import uk.co.autotrader.traverson.http.TextBody;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationTest {
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
    public void requestBody_SimpleTextBodyIsSerializedAndPostedCorrectly() {
        wireMockServer.stubFor(patch(urlEqualTo("/records/1"))
                .willReturn(status(202)));
        var didCheck = traverson.from("http://localhost:8089/records/1")
                .patch(new TextBody("{\"key\":123}", "application/json", StandardCharsets.UTF_8), response -> {
                    wireMockServer.verify(1, patchRequestedFor(urlEqualTo("/records/1")).withRequestBody(equalToJson("{\"key\":123}")));
                    assertThat(response.getStatusCode()).isEqualTo(202);
                    return true;
                });
        assertThat(didCheck).isTrue();
    }

    @Test
    public void requestBody_MultipartBodyIsSerializedAndPostedCorrectly() {
        byte[] data = new byte[]{0x00, 0x01, 0x02};
        wireMockServer.stubFor(post("/records")
                .withMultipartRequestBody(aMultipart()
                        .withName("my-body-part")
                        .withHeader("Content-Type", equalTo("application/octet-stream"))
                        .withBody(binaryEqualTo(data))
                )
                .willReturn(WireMock.status(202)));
        SimpleMultipartBody.BodyPart bodyPart = new SimpleMultipartBody.BodyPart("my-body-part", data, "application/octet-stream", "my-file");
        SimpleMultipartBody multipartBody = new SimpleMultipartBody(bodyPart);
        var didCheck = traverson.from("http://localhost:8089/records")
                .post(multipartBody, response -> {
                    wireMockServer.verify(1, postRequestedFor(urlEqualTo("/records")));
                    assertThat(response.getStatusCode()).isEqualTo(202);
                    return true;
                });
        assertThat(didCheck).isTrue();
    }

    @Test
    public void basicAuthentication_ReactsToUnauthorizedStatusAndAuthenticateHeader() {
        wireMockServer.stubFor(get("/restricted-area")
                .inScenario("Restricted access").whenScenarioStateIs(STARTED)
                .willSetStateTo("First request made")
                .willReturn(unauthorized().withHeader("WWW-Authenticate", "Basic realm=\"User Visible Realm\"")));

        wireMockServer.stubFor(get("/restricted-area")
                .inScenario("Restricted access").whenScenarioStateIs("First request made")
                .withBasicAuth("MyUsername", "MyPassword")
                .willReturn(ok()));

        var didCheck = traverson.from("http://localhost:8089/restricted-area")
                .withAuth("MyUsername", "MyPassword", "http://localhost:8089")
                .get(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(200);
                    wireMockServer.verify(2, getRequestedFor(urlEqualTo("/restricted-area")));
                    return true;
                });
        assertThat(didCheck).isTrue();
    }

    @Test
    public void basicAuthentication_GivenPreemptiveAuthenticationSetToTrue_SendsUsernameAndPasswordWithoutNeedingAnUnauthorizedResponse() {
        wireMockServer.stubFor(get("/restricted-area")
                .withBasicAuth("MyUsername", "MyPassword")
                .willReturn(ok()));

        var didCheck = traverson.from("http://localhost:8089/restricted-area")
                .withAuth("MyUsername", "MyPassword", "http://localhost:8089", true)
                .get(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(200);
                    wireMockServer.verify(1, getRequestedFor(urlEqualTo("/restricted-area")));
                    return true;
                });
        assertThat(didCheck).isTrue();
    }

    @Test
    public void requestBody_nonSuccessStatus_providesStringResponse() {
        wireMockServer.stubFor(get(urlEqualTo("/path"))
                .willReturn(WireMock.badRequest().withBody("error message")));
        var didCheck = traverson.from("http://localhost:8089/path")
                .get(response -> {
                    wireMockServer.verify(1, getRequestedFor(urlEqualTo("/path")));
                    assertThat(response.getResource(String.class)).isEqualTo("error message");
                    return true;
                });
        assertThat(didCheck).isTrue();
    }

    @Test
    public void requestBody_get_deserializedToObject() {
        wireMockServer.stubFor(get(urlEqualTo("/path"))
                .willReturn(ok().withBody("{\"key\":\"value\"}")));
        var resource = traverson.from("http://localhost:8089/path")
                .get(response -> {
                    wireMockServer.verify(1, getRequestedFor(urlEqualTo("/path")));

                    return response.getResource(TestResource.class);
                });
        assertThat(resource).isEqualTo(new TestResource("value"));
    }

    public static class TestResource {
        private String key;

        public TestResource() {
        }

        public TestResource(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestResource that = (TestResource) o;
            return Objects.equals(key, that.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    }
}
