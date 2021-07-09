package uk.co.autotrader.traverson.test;

import com.github.tomakehurst.wiremock.WireMockServer;

import java.util.ArrayList;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

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
    //TODO decide on follow() return or separate method for follow wiremock verifications

    public FollowVerification follow(String... rels) {
        //TODO consider multiple follows registered on same wiremock server - maybe introduce a base path to work off of for each follow setup
        //TODO last href pointing to endpoint like .../final rather than an integer (i.e .../3)
        var requestsToVerify = new ArrayList<Runnable>();
        for (int i = 0; i < rels.length; ++i) {
            var rel = rels[i];
            var stubUrl = "/" + (i == 0 ? "" : String.valueOf(i));
            var nextUrl = "/" + (i == rels.length - 1 ? "resource": String.valueOf(i + 1));
            var stubBody = relHandler.handle(rel, nextUrl);
            wireMockServer.stubFor(get(stubUrl).willReturn(okJson(stubBody)
                    .withHeader("Content-Type", "application/hal+json")));
            requestsToVerify.add(() -> wireMockServer.verify(getRequestedFor(urlEqualTo(stubUrl))));
        }
        return () -> requestsToVerify.forEach(Runnable::run);
    }

    public void verifyFollowsCalled(int followChainLength) {
        for (int i = 0; i < followChainLength; i++) {
            var url = "/" + (i == 0 ? "" : String.valueOf(i));
            wireMockServer.verify(getRequestedFor(urlEqualTo(url)));
        }
    }
}
