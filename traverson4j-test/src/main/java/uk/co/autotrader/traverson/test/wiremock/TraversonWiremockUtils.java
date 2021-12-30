package uk.co.autotrader.traverson.test.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;

import java.util.ArrayList;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class TraversonWiremockUtils {
    private static final RelHandler relHandler = new ArrayIndexRelHandler(
            new ArrayPropertyRelHandler(
                    new LinkRelHandler(null)));

    public static FollowVerification follow(WireMockServer server, String... rels) {
        //TODO consider multiple follows registered on same wiremock server - maybe introduce a base path to work off of for each follow setup
        // needs more consideration. Ordinarily would use scenarios for stateful wiremock behaviour, but, as both traverson
        // calls would be made in same line invocation in test, this is not possible.
        // Solution would probably be library user-side to have the requests go to separate servers during testing,
        // even if they would go to the same service when the app is deployed.
        var requestsToVerify = new ArrayList<Runnable>();
        for (int i = 0; i < rels.length; ++i) {
            var rel = rels[i];
            var stubUrl = "/" + (i == 0 ? "" : String.valueOf(i));
            var nextUrl = "/" + (i == rels.length - 1 ? "resource": String.valueOf(i + 1));
            var stubBody = relHandler.handle(server.baseUrl(), rel, nextUrl);
            server.stubFor(get(urlPathEqualTo(stubUrl)).willReturn(okJson(stubBody)
                    .withHeader("Content-Type", "application/hal+json")));
            requestsToVerify.add(() -> server.verify(getRequestedFor(urlEqualTo(stubUrl))));
        }
        return () -> requestsToVerify.forEach(Runnable::run);
    }

    public static void verifyFollowsCalled(WireMockServer server, int followChainLength) {
        for (int i = 0; i < followChainLength; i++) {
            var url = "/" + (i == 0 ? "" : String.valueOf(i));
            server.verify(getRequestedFor(urlPathEqualTo(url)));
        }
    }
}
