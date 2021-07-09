package uk.co.autotrader.traverson.http.utils;

abstract class RelHandler {
    private final RelHandler nextHandler;

    RelHandler(RelHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    abstract String handle(String rel, int relIndex);

    String delegateToNextHandler(String rel, int relIndex) {
        if (nextHandler != null) {
            return nextHandler.handle(rel, relIndex);
        }
        throw new IllegalArgumentException(String.format("Rel %s at index %d has not been handled. Make sure it has been correctly formatted", rel, relIndex));
    }
}
