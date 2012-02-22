/**
 * AuthenticatedClientHttpRequestFactory
 * (C) 2011 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on 11/30/11 by rherri01
 */
package org.nrg.framework.net;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.springframework.http.client.CommonsClientHttpRequestFactory;

public class AuthenticatedClientHttpRequestFactory extends CommonsClientHttpRequestFactory {
    private final String _user;
    private final String _password;

    public AuthenticatedClientHttpRequestFactory(String user, String password) {
        _user = user;
        _password = password;
    }

    @Override
    public HttpClient getHttpClient() {
        HttpClient client = super.getHttpClient();

        if (_user != null) {
            client.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(_user, _password));
        }

        return client;
    }
}
