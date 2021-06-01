package uk.co.autotrader.traverson.http;

import com.alibaba.fastjson.JSONObject;
import uk.co.autotrader.traverson.conversion.ResourceConversionService;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class Response {
    private final ResourceConversionService conversionService;
    private int statusCode;
    private URI uri;
    private InputStream resourceStream;
    private Map<String, String> responseHeaders = new HashMap<String, String>();

    public Response(ResourceConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public <T> T getResource(Class<T> clazz) {
        if (resourceStream == null) {
            return null;
        }
        return conversionService.convert(resourceStream, clazz);
    }

    public JSONObject getResource() {
        return getResource(JSONObject.class);
    }

    public void setResourceStream(InputStream resourceStream) {
        this.resourceStream = resourceStream;
    }

    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Map<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public boolean isSuccessful() {
        return statusCode / 100 == 2;
    }

    public void addResponseHeader(String name, String value) {
        this.responseHeaders.put(name, value);
    }
}
