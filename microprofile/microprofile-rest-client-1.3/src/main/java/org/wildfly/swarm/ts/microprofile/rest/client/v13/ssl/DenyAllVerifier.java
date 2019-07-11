package org.wildfly.swarm.ts.microprofile.rest.client.v13.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class DenyAllVerifier implements HostnameVerifier {
    @Override
    public boolean verify(String s, SSLSession sslSession) {
        return false;
    }
}
