package uk.co.autotrader.traverson.test;

abstract class RelHandler {
    private final RelHandler nextHandler;

    RelHandler(RelHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    abstract String handle(String rel, String nextUrl);

    String delegateToNextHandler(String rel, String nextUrl) {
        if (nextHandler != null) {
            return nextHandler.handle(rel, nextUrl);
        }
        throw new IllegalArgumentException(String.format("Rel \"%s\" has not been handled. Make sure it has been correctly formatted", rel));
    }
}
