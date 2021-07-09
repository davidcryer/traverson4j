package uk.co.autotrader.traverson.test;

import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;

public class TraversonFollowTestUtil {
    private final WireMockServer wireMockServer;
    private final RelHandler relHandler;

    public TraversonFollowTestUtil(WireMockServer wireMockServer) {
        this.wireMockServer = wireMockServer;
        var baseUrl = wireMockServer.baseUrl();
        relHandler = new ArrayIndexRelHandler(baseUrl,
                new ArrayPropertyRelHandler(baseUrl,
                        new LinkRelHandler(baseUrl, null)));
    }

    public void follow(String... rels) {//TODO return assertions runnable
        for (int i = 0; i < rels.length; ++i) {
            var rel = rels[i];
            var stubUrl = "/" + (i == 0 ? "" : String.valueOf(i));
            var stubBody = relHandler.handle(rel, i);
            wireMockServer.stubFor(get(stubUrl).willReturn(okJson(stubBody)
                    .withHeader("Content-Type", "application/hal+json")));
        }
    }
}
