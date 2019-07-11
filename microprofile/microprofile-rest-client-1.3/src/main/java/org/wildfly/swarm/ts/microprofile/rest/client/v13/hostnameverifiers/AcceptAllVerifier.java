package org.wildfly.swarm.ts.microprofile.rest.client.v13.hostnameverifiers;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class AcceptAllVerifier implements HostnameVerifier {
    @Override
    public boolean verify(String s, SSLSession sslSession) {
        return true;
    }
}
