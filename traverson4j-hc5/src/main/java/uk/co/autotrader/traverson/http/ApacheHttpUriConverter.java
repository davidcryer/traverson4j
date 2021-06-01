package uk.co.autotrader.traverson.http;


import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import uk.co.autotrader.traverson.conversion.ResourceConversionService;
import uk.co.autotrader.traverson.http.entity.BodyFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class ApacheHttpUriConverter {

    private final BodyFactory bodyFactory;
    private final TemplateUriUtils templateUriUtils;
    private final ResourceConversionService conversionService;

    public ApacheHttpUriConverter(BodyFactory bodyFactory, TemplateUriUtils templateUriUtils, ResourceConversionService conversionService) {
        this.bodyFactory = bodyFactory;
        this.templateUriUtils = templateUriUtils;
        this.conversionService = conversionService;
    }

    public ClassicHttpRequest toRequest(Request request) {
        Map<String, List<String>> templateParams = request.getTemplateParams();
        String uri = templateUriUtils.expandTemplateUri(request.getUrl(), templateParams);

        ClassicRequestBuilder requestBuilder = ClassicRequestBuilder.create(request.getMethod().name()).setUri(uri);

        request.getQueryParameters().forEach((key, values) -> values.forEach((value) -> requestBuilder.addParameter(key, value)));

        request.getHeaders().forEach(requestBuilder::addHeader);

        requestBuilder.addHeader("Accept", request.getAcceptMimeType());

        Body body = request.getBody();
        if (body != null) {
            requestBuilder.setEntity(bodyFactory.toEntity(body));
        }
        return requestBuilder.build();
    }


    public Response toResponse(CloseableHttpResponse httpResponse, HttpRequest httpRequest) throws IOException {
        Response response = new Response(conversionService);
        try {
            response.setUri(httpRequest.getUri());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("The http request contains an invalid URI", e);
        }
        response.setStatusCode(httpResponse.getCode());
        for (Header responseHeader : httpResponse.getHeaders()) {
            response.addResponseHeader(responseHeader.getName(), responseHeader.getValue());
        }

        HttpEntity httpEntity = httpResponse.getEntity();
        if (httpEntity != null) {
            response.setResourceStream(httpEntity.getContent());
        }
        return response;
    }
}
