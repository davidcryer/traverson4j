package uk.co.autotrader.traverson.http;

public interface ResponseHandler<T> {
    T handle(Response response);
}
