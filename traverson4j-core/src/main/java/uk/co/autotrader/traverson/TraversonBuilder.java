package uk.co.autotrader.traverson;

import com.alibaba.fastjson.JSONObject;
import uk.co.autotrader.traverson.http.AuthCredential;
import uk.co.autotrader.traverson.exception.IllegalHttpStatusException;
import uk.co.autotrader.traverson.http.*;
import uk.co.autotrader.traverson.link.BasicLinkDiscoverer;
import uk.co.autotrader.traverson.link.hal.HalLinkDiscoverer;
import uk.co.autotrader.traverson.link.LinkDiscoverer;

import java.util.*;

/**
 * Not thread safe
 *
 * <p>A builder which constructs a specification for interacting with a REST API
 * conforming to <a href="https://tools.ietf.org/html/draft-kelly-json-hal-03">
 * JSON HAL standards</a>
 *
 * @author Michael Rocke
 */
public class TraversonBuilder {
    private TraversonClient traversonClient;
    private LinkDiscoverer linkDiscoverer;
    private Deque<String> relsToFollow;
    private Request request;

    TraversonBuilder(TraversonClient traversonClient) {
        this.traversonClient = traversonClient;
        relsToFollow = new LinkedList<>();
        request = new Request();
    }

    public TraversonBuilder from(String startingUrl) {
        request.setUrl(startingUrl);
        return this;
    }

    public TraversonBuilder json() {
        this.request.setAcceptMimeType("application/json");
        this.linkDiscoverer = new BasicLinkDiscoverer();
        return this;
    }

    public TraversonBuilder jsonHal() {
        this.request.setAcceptMimeType("application/hal+json");
        this.linkDiscoverer = new HalLinkDiscoverer();
        return this;
    }

    public TraversonBuilder follow(String... rels) {
        this.relsToFollow.clear();
        this.relsToFollow.addAll(Arrays.asList(rels));
        return this;
    }

    /**
     * A builder method for adding query parameters to the web request. This
     * method is additive and does not overwrite query param key/values already
     * passed to the builder.
     *
     * @param name the query parameter key
     * @param values a var args of values to associate with the key
     * @return the current builder inclusive of new values for query parameters
     */
    public TraversonBuilder withQueryParam(String name, String... values) {
        this.request.addQueryParam(name, values);
        return this;
    }

    /**
     * A builder method for adding template parameters to the web request. This
     * method is additive and does not overwrite template param key/values already
     * passed to the builder.
     *
     * @param name the template parameter key
     * @param values a var args of values to associate with the key
     * @return the current builder inclusive of new values for template parameters
     */
    public TraversonBuilder withTemplateParam(String name, String... values) {
        this.request.addTemplateParam(name, values);
        return this;
    }

    /**
     * A builder method for adding headers to the web request. This
     * method is not additive and will overwrite header values with the
     * same key.
     *
     * @param name the name of the header key
     * @param value the value to associate with the key
     * @return the current builder inclusive of the header requested
     */
    public TraversonBuilder withHeader(String name, String value) {
        this.request.addHeader(name, value);
        return this;
    }

    /**
     * Apply the following basic auth credentials on all http requests
     * @param username the username
     * @param password the password
     * @return the current builder inclusive of auth credentials
     */
    public TraversonBuilder withAuth(String username, String password) {
        return withAuth(username, password, null);
    }

    /**
     * Apply the following basic auth credentials for only http requests on the supplied hostname
     * @param username the username
     * @param password the password
     * @param hostname simple definition of a hostname, e.g. "myservice.autotrader.co.uk"
     * @return the current builder inclusive of auth credentials
     */
    public TraversonBuilder withAuth(String username, String password, String hostname) {
        return withAuth(username, password, hostname, false);
    }

    /**
     * Apply the following basic auth credentials for only http requests on the supplied hostname
     * @param username the username
     * @param password the password
     * @param hostname simple definition of a hostname, e.g. "myservice.autotrader.co.uk"
     * @param preemptiveAuthentication when true, we preemptively send the username and password to the server instead of reacting to a unauthorized/401 response
     * @return the current builder inclusive of auth credentials
     */
    public TraversonBuilder withAuth(String username, String password, String hostname, boolean preemptiveAuthentication) {
        this.request.addAuthCredential(new AuthCredential(username, password, hostname, preemptiveAuthentication));
        return this;
    }

    /**
     * Navigate the path and get the response
     *
     * @return Response representing the http response and resource
     * @throws uk.co.autotrader.traverson.exception.UnknownRelException When navigating a path, a given rel cannot be found
     * @throws uk.co.autotrader.traverson.exception.IllegalHttpStatusException When a non 2xx response is returned part way through traversing
     * @throws uk.co.autotrader.traverson.exception.HttpException When the underlying http client experiences an issue with a request. This could be an intermittent issue
     */
    public <T> T get(ResponseHandler<T> responseHandler) {
        return traverseAndPerform(Method.GET, null, responseHandler);
    }

    /**
     * Navigate the path and delete the resource
     *
     * @return Response representing the http response
     * @throws uk.co.autotrader.traverson.exception.UnknownRelException When navigating a path, a given rel cannot be found
     * @throws uk.co.autotrader.traverson.exception.IllegalHttpStatusException When a non 2xx response is returned part way through traversing
     * @throws uk.co.autotrader.traverson.exception.HttpException When the underlying http client experiences an issue with a request. This could be an intermittent issue
     */
    public <T> T delete(ResponseHandler<T> responseHandler) {
        return traverseAndPerform(Method.DELETE, null, responseHandler);
    }

    /**
     * Navigate the path and post the body to the resource
     *
     * @param body request body to send
     * @return Response representing the http response and resource
     * @throws uk.co.autotrader.traverson.exception.UnknownRelException When navigating a path, a given rel cannot be found
     * @throws uk.co.autotrader.traverson.exception.IllegalHttpStatusException When a non 2xx response is returned part way through traversing
     * @throws uk.co.autotrader.traverson.exception.HttpException When the underlying http client experiences an issue with a request. This could be an intermittent issue
     */
    public <T> T post(Body body, ResponseHandler<T> responseHandler) {
        return traverseAndPerform(Method.POST, body, responseHandler);
    }

    /**
     * Navigate the path and put the body to the resource
     *
     * @param body request body to send
     * @return Response representing the http response
     * @throws uk.co.autotrader.traverson.exception.UnknownRelException When navigating a path, a given rel cannot be found
     * @throws uk.co.autotrader.traverson.exception.IllegalHttpStatusException When a non 2xx response is returned part way through traversing
     * @throws uk.co.autotrader.traverson.exception.HttpException When the underlying http client experiences an issue with a request. This could be an intermittent issue
     */
    public <T> T put(Body body, ResponseHandler<T> responseHandler) {
        return traverseAndPerform(Method.PUT, body, responseHandler);
    }

    /**
     * Navigate the path and patch the body to the resource
     *
     * @param body request body to send
     * @return Response representing the http response
     * @throws uk.co.autotrader.traverson.exception.UnknownRelException When navigating a path, a given rel cannot be found
     * @throws uk.co.autotrader.traverson.exception.IllegalHttpStatusException When a non 2xx response is returned part way through traversing
     * @throws uk.co.autotrader.traverson.exception.HttpException When the underlying http client experiences an issue with a request. This could be an intermittent issue
     */
    public <T> T patch(Body body, ResponseHandler<T> responseHandler) {
        return traverseAndPerform(Method.PATCH, body, responseHandler);
    }

    private <T> T traverseAndPerform(Method terminalMethod, Body terminalBody, ResponseHandler<T> responseHandler) {
        while (!relsToFollow.isEmpty()) {
            request.setMethod(Method.GET);
            traversonClient.execute(request, response -> {
                if (response.isSuccessful()) {
                    request.setUrl(linkDiscoverer.findHref(response.getResource(JSONObject.class), relsToFollow.removeFirst()));
                } else {
                    throw new IllegalHttpStatusException(response.getStatusCode(), response.getUri());
                }
                return null;
            });
        }

        request.setBody(terminalBody);
        request.setMethod(terminalMethod);
        return traversonClient.execute(request, responseHandler);
    }
}
