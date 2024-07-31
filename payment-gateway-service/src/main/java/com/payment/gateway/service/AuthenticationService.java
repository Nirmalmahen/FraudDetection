package com.payment.gateway.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.NullInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.kafka.common.errors.AuthenticationException;
import org.apache.kafka.common.errors.InvalidRequestException;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.logging.ErrorManager;
import java.util.stream.Collectors;

import static java.util.Base64.getEncoder;
import static java.util.jar.Attributes.Name.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;


public class AuthenticationService {

    private final String GSAUTN_AGENT;
    private final String principal;
    private final String keytabPath;
    private final String pingClientId;
    private final String pingClientSecretName;
    private final String pingUrl;
    private final String accessTokenManagerId;
    private final SecretsManager secretsManager;

    public AuthenticationService(String gsautnAgent,
                                 String principal,
                                 String keytabPath,
                                 String pingClientId,
                                 String pingClientSecretName,
                                 String pingUrl,
                                 String accessTokenManagerId,
                                 SecretsManager secretsManager) {
        GSAUTN_AGENT = gsautnAgent;
        this.principal = principal;
        this.keytabPath = keytabPath;
        this.pingClientId = pingClientId;
        this.pingClientSecretName = pingClientSecretName;
        this.pingUrl = pingUrl;
        this.accessTokenManagerId = accessTokenManagerId;
        this.secretsManager = secretsManager;
    }

    public String getOauthToken() {
        ObjectMapper mapper = new ObjectMapper();
        String access_token = null;

        try {
            String clientSecret = secretsManager.getSecretVaLueResult(pingClientSecretName);
            String to_encode = pingClientId + ":" + clientSecret;
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost httpPost = new HttpPost(pingUrl);
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("grant_type", "client_credentials"));
                params.add(new BasicNameValuePair("access_token_manager_id", accessTokenManagerId));
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                httpPost.setHeader(String.valueOf(CONTENT_TYPE), "application/x-www-form-urlencoded");
                httpPost.setHeader(AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString(to_encode.getBytes()));
                try (CloseableHttpResponse response = client.execute(httpPost)) {
                    InputStream responseStream = response.getEntity() == null ? new NullInputStream(0) : response.getEntity().getContent();
                    Map<String, String> obj = mapper.readValue(responseStream, Map.class);
                    access_token = obj.get("access_token");
                } catch (Exception ex) {
                }
            } catch (Exception ex) {
            }
        } catch (Exception e) {
        }
        return access_token;
    }

    public Map<String, String> getOauthTokenMap() {
        Map<String, String> authTokenMap = new HashMap<>();
        try {
            String access_token = getOauthToken();
            String gsssoClaim = getGSSSOClaim(access_token);
            if (StringUtils.isNotBlank(access_token) && StringUtils.isNotBlank(gsssoClaim)) {
                authTokenMap.put("access_token", access_token);
                authTokenMap.put("gssso", gsssoClaim);
            } else {
                throw new AuthenticationException("Access Token or OSSSO Token is blank due to error in call to PingFederate!");
            }
        } catch (Exception e) {
        }
        return authTokenMap;
    }

    public String getGSSSOClaim(String access_token) {
        List<String> claimList = decodeTokenParts(access_token);
        Map<String, String> cLaimMap = claimList.stream().filter(claim -> StringUtils.contains(claim, ":"))
                .collect
                        (Collectors.toMap(claim -> claim.split(":")[0].replaceAll("\"", ""),
                                claim -> claim.split(":")[1].replaceAll("\"", "")));
        return cLaimMap.getOrDefault("gssso", "");
    }

    public List<String> decodeTokenParts(String accessToken) {
        List<String> claimList = new ArrayList<>();
        if (StringUtils.isNotBlank(accessToken)) {
            String[] tokenParts = accessToken.split("\\.", 0);
            for (String part : tokenParts) {
                try {
                    byte[] decodedBytes = Base64.getUrlDecoder().decode(part);
                    String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
                    decodedString = StringUtils.stripStart(decodedString, "{");
                    decodedString = StringUtils.stripStart(decodedString, "}");
                    claimList.addAll(Arrays.asList(decodedString.split(",")));
                } catch (Exception ex) {
                }
            }
        }
        return claimList;
    }

    @Component
    public class SecretsManager {

        @Autowired

        public String getSecretVaLueResult(String secretName) {

            return "secret";

        }
    }
}

