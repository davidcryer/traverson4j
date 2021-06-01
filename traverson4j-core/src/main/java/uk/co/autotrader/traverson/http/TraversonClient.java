package uk.co.autotrader.traverson.http;

public interface TraversonClient {

    //TODO: comment how it will throw Http Exception
    <T> T execute(Request request, ResponseHandler<T> responseHandler);
}
