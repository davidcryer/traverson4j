package uk.co.autotrader.traverson.test.wiremock;

class LinkRelHandler extends RelHandler {

    LinkRelHandler(RelHandler nextHandler) {
        super(nextHandler);
    }

    @Override
    String handle(String baseUrl, String rel, String nextUrl) {
        return String.format("{\"_links\":{\"%1$s\":{\"href\":\"%2$s%3$s\"}}}", rel, baseUrl, nextUrl);
    }
}
