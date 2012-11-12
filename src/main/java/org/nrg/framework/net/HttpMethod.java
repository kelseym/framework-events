/**
 * HttpMethod
 * (C) 2012 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on 10/31/12 by rherrick
 */
package org.nrg.framework.net;

import org.apache.http.client.methods.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides enums for mapping HTTP request methods, as well as a static for retrieving the appropriate subclass of the
 * {@link HttpRequestBase} class for an indicated method.
 */
public enum HttpMethod {
    GET("GET"),
    PUT("PUT"),
    POST("POST"),
    DELETE("DELETE"),
    HEAD("HEAD"),
    TRACE("TRACE"),
    OPTIONS("OPTIONS");

    public static HttpMethod method(String method) {
        if (_methods.isEmpty()) {
            synchronized (HttpMethod.class) {
                for (HttpMethod httpMethod : values()) {
                    _methods.put(httpMethod.getMethod(), httpMethod);
                }
            }
        }
        return _methods.get(method);
    }

    public static HttpRequestBase getHttpRequestObject(String method) {
        return getHttpRequestObject(HttpMethod.method(method));
    }

    public static HttpRequestBase getHttpRequestObject(HttpMethod method) {
        HttpRequestBase request;

        switch (method) {
            case GET:
                request = new HttpGet();
                break;
            case PUT:
                request = new HttpPut();
                break;
            case POST:
                request = new HttpPost();
                break;
            case DELETE:
                request = new HttpDelete();
                break;
            case HEAD:
                request = new HttpHead();
                break;
            case TRACE:
                request = new HttpTrace();
                break;
            case OPTIONS:
                request = new HttpOptions();
                break;
            default:
                throw new RuntimeException("Unknown method specified: " + method);
        }

        return request;
    }

    private HttpMethod(String method) {
        _method = method;
    }

    private String getMethod() {
        return _method;
    }

    private static final Map<String, HttpMethod> _methods = new HashMap<String, HttpMethod>();

    private final String _method;

    @Override
    public String toString() {
        return this.name();
    }
}

