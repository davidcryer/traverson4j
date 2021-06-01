package uk.co.autotrader.traverson.http;

import com.alibaba.fastjson.util.IOUtils;
import org.apache.hc.client5.http.auth.AuthCache;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicAuthCache;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.auth.BasicScheme;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpHost;
import uk.co.autotrader.traverson.conversion.ResourceConversionService;
import uk.co.autotrader.traverson.exception.HttpException;
import uk.co.autotrader.traverson.http.entity.BodyFactory;

import java.io.IOException;
import java.net.URISyntaxException;

public class ApacheHttpTraversonClientAdapter implements TraversonClient {

    private final CloseableHttpClient adapterClient;
    private final ApacheHttpUriConverter apacheHttpUriConverter;
    private static final AuthScope AUTH_SCOPE_MATCHING_ANYTHING = new AuthScope(null, null, -1, null, null);


    public ApacheHttpTraversonClientAdapter() {
        this(HttpClients.createDefault());
    }

    public ApacheHttpTraversonClientAdapter(CloseableHttpClient client) {
        this.adapterClient = client;
        this.apacheHttpUriConverter = new ApacheHttpUriConverter(new BodyFactory(), new TemplateUriUtils(), ResourceConversionService.getInstance());
    }

    @Override
    public <T> T execute(Request request, ResponseHandler<T> responseHandler) {
        ClassicHttpRequest httpRequest = apacheHttpUriConverter.toRequest(request);

        HttpClientContext clientContext = HttpClientContext.create();
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        AuthCache authCache = new BasicAuthCache();

        for (AuthCredential authCredential : request.getAuthCredentials()) {
            constructCredentialsProviderAndAuthCache(credentialsProvider, authCache, authCredential);
        }

        clientContext.setCredentialsProvider(credentialsProvider);
        clientContext.setAuthCache(authCache);
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = adapterClient.execute(httpRequest, clientContext);
            var response = apacheHttpUriConverter.toResponse(httpResponse, httpRequest);
            return responseHandler.handle(response);
        } catch (IOException e) {
            throw new HttpException("Error with httpClient", e);
        } finally {
            IOUtils.close(httpResponse);
        }
    }

    void constructCredentialsProviderAndAuthCache(BasicCredentialsProvider credentialsProvider, AuthCache authCache, AuthCredential authCredential) {
        UsernamePasswordCredentials userPassword = new UsernamePasswordCredentials(authCredential.getUsername(), authCredential.getPassword().toCharArray());
        AuthScope authScope = AUTH_SCOPE_MATCHING_ANYTHING;
        if (authCredential.getHostname() != null) {
            HttpHost target;
            try {
                target = HttpHost.create(authCredential.getHostname());
                authScope = new AuthScope(target);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Preemptive authentication hostname is invalid", e);
            }

            if (authCredential.isPreemptiveAuthentication()) {
                BasicScheme authScheme = new BasicScheme();
                authScheme.initPreemptive(userPassword);
                authCache.put(target, authScheme);
            }
        }
        credentialsProvider.setCredentials(authScope, userPassword);
    }
}
