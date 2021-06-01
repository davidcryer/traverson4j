package uk.co.autotrader.traverson.http;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.hc.client5.http.auth.AuthCache;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.auth.BasicAuthCache;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.HttpHost;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.autotrader.traverson.exception.HttpException;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApacheHttpTraversonClientAdapterTest {
    private ApacheHttpTraversonClientAdapter clientAdapter;
    @Mock
    private CloseableHttpClient httpClient;
    @Mock
    private ApacheHttpUriConverter apacheHttpUriConverter;
    @Mock
    private HttpUriRequest httpRequest;
    @Mock
    private CloseableHttpResponse httpResponse;
    @Mock
    private AuthCredential authCredential;
    @Captor
    private ArgumentCaptor<HttpClientContext> clientContextCaptor;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private Request request;
    private Response expectedResponse;
    private static final AuthScope AUTH_SCOPE_ANY = new AuthScope(null, null, -1, null, null);

//    @Before
//    public void setUp() throws Exception {
//        clientAdapter = new ApacheHttpTraversonClientAdapter(httpClient);
//        FieldUtils.writeField(clientAdapter, "apacheHttpUriConverter", apacheHttpUriConverter, true);
//        request = new Request();
//        expectedResponse = new Response<>();
//        when(apacheHttpUriConverter.toRequest(request)).thenReturn(httpRequest);
//        when(apacheHttpUriConverter.toResponse(httpResponse, httpRequest, JSONObject.class)).thenReturn(expectedResponse);
//    }
//
//    @Test
//    public void execute_GivenGetRequest_ReturnsResponse() throws Exception {
//        when(httpClient.execute(eq(httpRequest), any(HttpClientContext.class))).thenReturn(httpResponse);
//
//        Response<JSONObject> response = clientAdapter.execute(request, JSONObject.class);
//
//        assertThat(response).isEqualTo(expectedResponse);
//        verify(httpResponse).close();
//    }
//    @Test
//    public void execute_GivenIOExceptionIsThrown_WrapsInTraversonException() throws Exception {
//        when(httpClient.execute(eq(httpRequest), any(HttpClientContext.class))).thenReturn(httpResponse);
//        when(apacheHttpUriConverter.toResponse(httpResponse, httpRequest, JSONObject.class)).thenThrow(new IOException());
//        expectedException.expect(HttpException.class);
//
//        clientAdapter.execute(request, JSONObject.class);
//
//        verify(httpResponse).close();
//    }
//
//    @Test
//    public void execute_GivenRequestWithAuthCredentials() throws Exception {
//        when(httpClient.execute(eq(httpRequest), clientContextCaptor.capture())).thenReturn(httpResponse);
//        request.addAuthCredential(new AuthCredential("user", "password", null, false));
//
//        Response<JSONObject> response = clientAdapter.execute(request, JSONObject.class);
//
//        assertThat(response).isEqualTo(expectedResponse);
//        HttpClientContext clientContext = clientContextCaptor.getValue();
//        Credentials credentials = clientContext.getCredentialsProvider().getCredentials(AUTH_SCOPE_ANY, clientContext);
//        assertThat(credentials.getUserPrincipal().getName()).isEqualTo("user");
//        assertThat(credentials.getPassword()).isEqualTo("password".toCharArray());
//    }
//
//    @Test
//    public void execute_GivenRequestWithScopedAuthCredentials() throws Exception {
//        when(httpClient.execute(eq(httpRequest), clientContextCaptor.capture())).thenReturn(httpResponse);
//        request.addAuthCredential(new AuthCredential("user", "password", "myhost.autotrader.co.uk", false));
//
//        Response<JSONObject> response = clientAdapter.execute(request, JSONObject.class);
//
//        assertThat(response).isEqualTo(expectedResponse);
//        HttpClientContext clientContext = clientContextCaptor.getValue();
//        Credentials credentials = clientContext.getCredentialsProvider().getCredentials(AUTH_SCOPE_ANY, clientContext);
//        assertThat(credentials.getUserPrincipal().getName()).isEqualTo("user");
//        assertThat(credentials.getPassword()).isEqualTo("password".toCharArray());
//    }
//
//    @Test
//    public void execute_GivenMultipleAuthCredentials() throws Exception {
//        when(httpClient.execute(eq(httpRequest), clientContextCaptor.capture())).thenReturn(httpResponse);
//        request.addAuthCredential(new AuthCredential("user", "password", "myhost.autotrader.co.uk", false));
//        request.addAuthCredential(new AuthCredential("user2", "password2", "myhost.autotrader.co.uk", false));
//        request.addAuthCredential(new AuthCredential("user3", "password3", null, false));
//
//        Response<JSONObject> response = clientAdapter.execute(request, JSONObject.class);
//
//        assertThat(response).isEqualTo(expectedResponse);
//        HttpClientContext clientContext = clientContextCaptor.getValue();
//        Credentials credentials = clientContext.getCredentialsProvider().getCredentials(new AuthScope(new HttpHost("myhost.autotrader.co.uk")), clientContext);
//        assertThat(credentials.getUserPrincipal().getName()).isEqualTo("user2");
//        assertThat(credentials.getPassword()).isEqualTo("password2".toCharArray());
//        credentials = clientContext.getCredentialsProvider().getCredentials(new AuthScope(null, null, -1, null, null), clientContext);
//        assertThat(credentials.getUserPrincipal().getName()).isEqualTo("user3");
//        assertThat(credentials.getPassword()).isEqualTo("password3".toCharArray());
//        verify(httpResponse).close();
//    }
//
//    @Test
//    public void init_SetsDefaultHttpClient() throws Exception {
//        ApacheHttpTraversonClientAdapter apacheHttpTraversonClientAdapter = new ApacheHttpTraversonClientAdapter();
//
//        assertThat(FieldUtils.readField(apacheHttpTraversonClientAdapter, "adapterClient", true)).isNotNull();
//    }
//
//    @Test
//    public void constructCredentialsProviderAndAuthCache_ifNoHostnameReturnsAnyAuthScope() {
//        BasicCredentialsProvider basicCredentialsProvider = new BasicCredentialsProvider();
//        ApacheHttpTraversonClientAdapter apacheHttpTraversonClientAdapter = new ApacheHttpTraversonClientAdapter();
//        AuthCache authCache = new BasicAuthCache();
//
//        when(authCredential.getUsername()).thenReturn("username");
//        when(authCredential.getPassword()).thenReturn("password");
//
//        apacheHttpTraversonClientAdapter.constructCredentialsProviderAndAuthCache(basicCredentialsProvider, authCache, authCredential);
//
//        assertThat(basicCredentialsProvider.toString()).isEqualTo("{<any auth scheme> <any realm> <any protocol>://<any host>:<any port>=[principal: username]}");
//        assertThat(authCache.get(new HttpHost("hostname"))).isNull();
//    }
//
//    @Test
//    public void constructCredentialsProviderAndAuthCache_setsHostnameInAuthScope() {
//        BasicCredentialsProvider basicCredentialsProvider = new BasicCredentialsProvider();
//        ApacheHttpTraversonClientAdapter apacheHttpTraversonClientAdapter = new ApacheHttpTraversonClientAdapter();
//        AuthCache authCache = new BasicAuthCache();
//
//        when(authCredential.getUsername()).thenReturn("username");
//        when(authCredential.getPassword()).thenReturn("password");
//        when(authCredential.getHostname()).thenReturn("hostname");
//
//        apacheHttpTraversonClientAdapter.constructCredentialsProviderAndAuthCache(basicCredentialsProvider, authCache, authCredential);
//
//        assertThat(basicCredentialsProvider.toString()).isEqualTo("{<any auth scheme> <any realm> http://hostname:<any port>=[principal: username]}");
//        assertThat(authCache.get(new HttpHost("hostname"))).isNull();
//    }
//
//    @Test
//    public void constructCredentialsProviderAndAuthCache_setsHostnameAndBasicAuth() {
//        BasicCredentialsProvider basicCredentialsProvider = new BasicCredentialsProvider();
//        AuthCache authCache = new BasicAuthCache();
//        ApacheHttpTraversonClientAdapter apacheHttpTraversonClientAdapter = new ApacheHttpTraversonClientAdapter();
//
//        when(authCredential.getUsername()).thenReturn("username");
//        when(authCredential.getPassword()).thenReturn("password");
//        when(authCredential.getHostname()).thenReturn("hostname");
//        when(authCredential.isPreemptiveAuthentication()).thenReturn(true);
//
//        apacheHttpTraversonClientAdapter.constructCredentialsProviderAndAuthCache(basicCredentialsProvider, authCache, authCredential);
//
//        assertThat(basicCredentialsProvider.toString()).isEqualTo("{<any auth scheme> <any realm> http://hostname:<any port>=[principal: username]}");
//        assertThat(authCache.get(new HttpHost("hostname")).getName()).isEqualTo("Basic");
//    }
//
//    @Test
//    public void constructCredentialsProviderAndAuthCache_throwsExceptionWhenHostnameContainsSpaces() {
//        BasicCredentialsProvider basicCredentialsProvider = new BasicCredentialsProvider();
//        ApacheHttpTraversonClientAdapter apacheHttpTraversonClientAdapter = new ApacheHttpTraversonClientAdapter();
//        AuthCache authCache = new BasicAuthCache();
//
//        when(authCredential.getHostname()).thenReturn("hostname with spaces");
//        when(authCredential.getUsername()).thenReturn("username");
//        when(authCredential.getPassword()).thenReturn("password");
//
//        assertThatThrownBy(() -> apacheHttpTraversonClientAdapter.constructCredentialsProviderAndAuthCache(basicCredentialsProvider, authCache, authCredential))
//                .isInstanceOf(IllegalArgumentException.class)
//                .hasMessage("Preemptive authentication hostname is invalid")
//                .hasCauseInstanceOf(URISyntaxException.class);
//    }

}
