package com.khwish.backend.restapi;

import com.khwish.backend.utils.ParsingUtil;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RestApiManager {

    private static final String classTag = RestApiManager.class.getSimpleName();
    private static final Logger LOGGER = Logger.getLogger(classTag);

    private static RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());

    private static <T> T get(String url, HttpHeaders headers, Class<T> responseClass) throws HttpClientErrorException.Unauthorized {
        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);
        return makeHttpRequest(url, HttpMethod.GET, requestEntity, responseClass);
    }

    public static <T> T postOAuthForm(String url, String authToken, HashMap<String, String> body, Class<T> responseClass)
            throws HttpClientErrorException.Unauthorized {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(authToken);

        MultiValueMap<String, String> bodyForm = new LinkedMultiValueMap<>();
        for (Map.Entry<String, String> bodyEntry : body.entrySet()) {
            bodyForm.add(bodyEntry.getKey(), bodyEntry.getValue());
        }
        return post(url, headers, bodyForm, responseClass);
    }

    public static <T> T postForm(String url, HashMap<String, String> body, Class<T> responseClass) throws HttpClientErrorException.Unauthorized {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> bodyForm = new LinkedMultiValueMap<>();
        for (Map.Entry<String, String> bodyEntry : body.entrySet()) {
            bodyForm.add(bodyEntry.getKey(), bodyEntry.getValue());
        }
        return post(url, headers, bodyForm, responseClass);
    }

    public static <T> T post(String url, Object body, Class<T> responseClass) {
        return post(url, defaultHeaders(), body, responseClass);
    }

    private static <T> T post(String url, HttpHeaders headers, Object body, Class<T> responseClass) throws HttpClientErrorException.Unauthorized {
        HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);
        return makeHttpRequest(url, HttpMethod.POST, requestEntity, responseClass);
    }

    private static <T> T makeHttpRequest(String url, HttpMethod method, HttpEntity<Object> requestEntity, Class<T> responseClass)
            throws HttpClientErrorException.Unauthorized {
        ResponseEntity<T> response;
        int MAX_RETRY_COUNT = 5;
        int currentRetriesCount = 0;
        do {
            try {
                LOGGER.log(Level.INFO, "[" + classTag + "][makeHttpRequest] Making HTTP request on Url: " + url
                        + " Method: " + method + " RequestEntity: " + requestEntity);
                response = restTemplate.exchange(url, method, requestEntity, responseClass);
                if (response.hasBody()) {
                    LOGGER.log(Level.INFO, "[" + classTag + "][makeHttpRequest] " + response.getStatusCodeValue()
                            + " Response received. " + response.getBody());
                    return response.getBody();
                } else {
                    LOGGER.log(Level.INFO, "[" + classTag + "][makeHttpRequest] " + response.getStatusCodeValue()
                            + "  Response received. Response has no body");
                    return null;
                }
            } catch (ResourceAccessException | HttpServerErrorException | HttpClientErrorException.TooManyRequests e) {
                LOGGER.log(Level.INFO, "[" + classTag + "][makeHttpRequest] Retrying HTTP request for "
                        + currentRetriesCount + " time. Url: " + url + " Method: " + method + " RequestEntity: " + requestEntity);
                try {
                    Thread.sleep(((1 << currentRetriesCount) - 1) * 1000);
                } catch (InterruptedException ie) {
                    LOGGER.log(Level.ERROR, ie.toString(), ie);
                }
            } catch (HttpClientErrorException.Unauthorized unauthorizedException) {
                LOGGER.log(Level.ERROR, "[" + classTag + "][makeHttpRequest] " + unauthorizedException.getRawStatusCode()
                        + " " + unauthorizedException.getStatusText() + " Response received. " + unauthorizedException.getResponseBodyAsString());
                throw unauthorizedException;
            } catch (HttpStatusCodeException unsuccessfulRequestException) {
                if (unsuccessfulRequestException.getStatusCode().is5xxServerError()) {
                    LOGGER.log(Level.FATAL, "[" + classTag + "][makeHttpRequest] External Server Down: " + url);
                }

                String errorBodyString = unsuccessfulRequestException.getResponseBodyAsString();
                if (errorBodyString.isEmpty()) {
                    LOGGER.log(Level.ERROR, "[" + classTag + "][makeHttpRequest] " + unsuccessfulRequestException.getRawStatusCode()
                            + " " + unsuccessfulRequestException.getStatusText() + " Response received. Response has no error body");
                    return null;
                } else {
                    T errorBody = ParsingUtil.fromJson(unsuccessfulRequestException.getResponseBodyAsString(), responseClass);
                    LOGGER.log(Level.ERROR, "[" + classTag + "][makeHttpRequest] " + unsuccessfulRequestException.getRawStatusCode()
                            + " " + unsuccessfulRequestException.getStatusText() + " Response received. " + errorBody);
                    return errorBody;
                }
            }
        } while (++currentRetriesCount < MAX_RETRY_COUNT);
        return null;
    }

    private static HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return headers;
    }

    private static ClientHttpRequestFactory clientHttpRequestFactory() {
        int MAX_TOTAL_CONNECTIONS = 20;
        int MAX_TOTAL_CONNECTIONS_PER_ROUTE = 4;
        int HTTP_READ_TIMEOUT = 5000; //in milliseconds
        int HTTP_CONNECTION_TIMEOUT = 10000; //in milliseconds

        PoolingHttpClientConnectionManager poolingConManager = new PoolingHttpClientConnectionManager();
        poolingConManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        poolingConManager.setDefaultMaxPerRoute(MAX_TOTAL_CONNECTIONS_PER_ROUTE);

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(
                HttpClients.custom().setConnectionManager(poolingConManager).build());
        factory.setReadTimeout(HTTP_READ_TIMEOUT);
        factory.setConnectTimeout(HTTP_CONNECTION_TIMEOUT);
        return factory;
    }
}
