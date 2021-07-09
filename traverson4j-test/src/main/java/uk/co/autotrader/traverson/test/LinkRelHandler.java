package uk.co.autotrader.traverson.test;

class LinkRelHandler extends RelHandler {
    private final String baseUrl;

    LinkRelHandler(String baseUrl, RelHandler nextHandler) {
        super(nextHandler);
        this.baseUrl = baseUrl;
    }

    @Override
    String handle(String rel, int relIndex) {
        return String.format("{\"_links\":{\"%1$s\":{\"href\":\"%2$s/%3$s\"}}}", rel, baseUrl, relIndex + 1);
    }
}
