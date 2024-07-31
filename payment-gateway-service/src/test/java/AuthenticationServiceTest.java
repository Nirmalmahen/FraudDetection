
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.gateway.service.AuthenticationService;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpClients.class, HttpResponse.class, AuthenticationService.class})
class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private AuthenticationService.SecretsManager secretsManager;

    @Mock
    private CloseableHttpClient httpClient;

    @Mock
    private CloseableHttpResponse httpResponse;



    private final String GSAUTN_AGENT = "TestAgent";
    private final String principal = "TestPrincipal";
    private final String keytabPath = "/path/to/keytab";
    private final String pingClientId = "TestClientId";
    private final String pingClientSecretName = "TestSecretName";
    private final String pingUrl = "https://example.com/token";
    private final String accessTokenManagerId = "TestManagerId";

    @BeforeEach
    void setUp() throws IOException {
//        PowerMockito.mockStatic(HttpClients.class);
//        when(HttpClients.createDefault()).thenReturn(httpClient);
         httpClient = mock(CloseableHttpClient.class);
        httpResponse = mock(CloseableHttpResponse.class);
        when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);
        secretsManager=Mockito.mock(AuthenticationService.SecretsManager.class);
        authenticationService = new AuthenticationService(GSAUTN_AGENT, principal, keytabPath, pingClientId, pingClientSecretName, pingUrl, accessTokenManagerId, secretsManager);
    }

    @Test
    void testGetOauthToken_Success() throws Exception {
        // Arrange
        String clientSecret = "TestSecret";
        String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MTc1ODg2MjcsImlhdCI6MTYxNzU4NTAyNywibmJmIjoxNjE3NTg1MDI3LCJqdGkiOiI4ZDg5NTFiMC0zM2VmLTQxY2YtYWFhMC1mYTg5ZDJhNTEwMTYiLCJjbGllbnRfaWQiOiJjbGllbnRfaWQiLCJzY29wZXMiOlsiYXBwIl0sImdzc3NvIjoiZ3Nzc29fY2xhaW0ifQ.Vpnd8gsiY0Hb-XwJd62QOggJ2n6R8osS_eNVvMzCO5E";

        // Mock secretsManager
        when(secretsManager.getSecretVaLueResult(pingClientSecretName)).thenReturn(clientSecret);

        // Mock HTTP response
        when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("access_token", accessToken);
        String jsonResponse = new ObjectMapper().writeValueAsString(responseMap);
        when(httpResponse.getEntity()).thenReturn(new StringEntity(jsonResponse, ContentType.APPLICATION_JSON));

        // Act
        String result = authenticationService.getOauthToken();

        // Assert
        assertNotNull(result, "Access token should not be null");
        assertEquals(accessToken, result);

    }

    @Test
    void testGetOauthToken_ExceptionHandling() throws Exception {
        // Arrange
        String clientSecret = "TestSecret";

        // Mock secretsManager
        when(secretsManager.getSecretVaLueResult(pingClientSecretName)).thenReturn(clientSecret);

        // Mock HTTP response to throw an IOException
        when(httpClient.execute(any(HttpPost.class))).thenThrow(new IOException("HTTP request failed"));

        // Act
        String result = authenticationService.getOauthToken();

        // Assert
        assertNull(result, "Access token should be null due to exception");
    }

    @Test
    void testGetOauthToken_NullResponseStream() throws Exception {
        // Arrange
        String clientSecret = "TestSecret";

        // Mock secretsManager
        when(secretsManager.getSecretVaLueResult(pingClientSecretName)).thenReturn(clientSecret);

        // Mock HTTP response with null entity
        when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);
        when(httpResponse.getStatusLine().getStatusCode()).thenReturn(200);
        when(httpResponse.getEntity()).thenReturn(null);

        // Act
        String result = authenticationService.getOauthToken();

        // Assert
        assertNull(result, "Access token should be null when response is null");
    }

    @Test
    void testGetOauthTokenMap_Success() throws Exception {
        // Arrange
        String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MTc1ODg2MjcsImlhdCI6MTYxNzU4NTAyNywibmJmIjoxNjE3NTg1MDI3LCJqdGkiOiI4ZDg5NTFiMC0zM2VmLTQxY2YtYWFhMC1mYTg5ZDJhNTEwMTYiLCJjbGllbnRfaWQiOiJjbGllbnRfaWQiLCJzY29wZXMiOlsiYXBwIl0sImdzc3NvIjoiZ3Nzc29fY2xhaW0ifQ.Vpnd8gsiY0Hb-XwJd62QOggJ2n6R8osS_eNVvMzCO5E";
        String gsssoClaim = "gssso_claim_value";

        // Mock getOauthToken method
        AuthenticationService spyService = Mockito.spy(authenticationService);
        doReturn(accessToken).when(spyService).getOauthToken();

        // Mock getGSSSOClaim method
        doReturn(gsssoClaim).when(spyService).getGSSSOClaim(accessToken);

        // Act
        Map<String, String> resultMap = spyService.getOauthTokenMap();

        // Assert
        assertNotNull(resultMap, "Token map should not be null");
        assertEquals(accessToken, resultMap.get("access_token"));
        assertEquals(gsssoClaim, resultMap.get("gssso"));
    }

    @Test
    void testGetOauthTokenMap_Failure() {
        // Arrange
        String accessToken = "invalid_token";

        // Mock getOauthToken method
        AuthenticationService spyService = Mockito.spy(authenticationService);
        doReturn(accessToken).when(spyService).getOauthToken();

        // Mock getGSSSOClaim method
        doReturn(null).when(spyService).getGSSSOClaim(accessToken);

        // Act
        Map<String, String> resultMap = spyService.getOauthTokenMap();

        // Assert
        assertTrue(resultMap.isEmpty(), "Token map should be empty due to failure");
    }

    @Test
    void testGetGSSSOClaim_Success() {
        // Arrange
        String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJnZHNzbyI6Imdzc3NvX2NsYWltIn0.8IbY8L-ihM55XqJp9gY3K1zk9j7fL7tFgW5f5QiURQw";
        String expectedClaim = "gssso_claim";

        // Act
        String claim = authenticationService.getGSSSOClaim(accessToken);

        // Assert
        assertEquals(expectedClaim, claim);
    }

    @Test
    void testGetGSSSOClaim_NoClaimFound() {
        // Arrange
        String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJub25jbGFpbSI6Im5vbmV4aXN0ZW50Y2xhaW0ifQ.Vpnd8gsiY0Hb-XwJd62QOggJ2n6R8osS_eNVvMzCO5E";

        // Act
        String claim = authenticationService.getGSSSOClaim(accessToken);

        // Assert
        assertEquals("", claim, "Expected empty claim");
    }

    @Test
    void testDecodeTokenParts_Success() {
        // Arrange
        String accessToken = "eyJrZXkiOiJ2YWx1ZSJ9.eyJrZXkyIjoiYW5vdGhlclZhbHVlIn0.eyJrZXkzIjoidGhpcmRWYWx1ZSJ9";
        List<String> expectedClaims = List.of("key:value", "key2:anotherValue", "key3:thirdValue");

        // Act
        List<String> claims = authenticationService.decodeTokenParts(accessToken);

        // Assert
        assertEquals(expectedClaims, claims);
    }

    @Test
    void testDecodeTokenParts_InvalidBase64() {
        // Arrange
        String invalidAccessToken = "invalid_base64_token";

        // Act
        List<String> claims = authenticationService.decodeTokenParts(invalidAccessToken);

        // Assert
        assertTrue(claims.isEmpty(), "Expected empty claims list for invalid base64 token");
    }

    @Test
    void testDecodeTokenParts_EmptyToken() {
        // Act
        List<String> claims = authenticationService.decodeTokenParts("");

        // Assert
        assertTrue(claims.isEmpty(), "Expected empty claims list for empty token");
    }
}
