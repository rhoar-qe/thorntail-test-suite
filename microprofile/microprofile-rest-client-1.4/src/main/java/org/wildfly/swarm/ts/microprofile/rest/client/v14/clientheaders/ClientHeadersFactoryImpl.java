package org.wildfly.swarm.ts.microprofile.rest.client.v14.clientheaders;

import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;


public class ClientHeadersFactoryImpl implements ClientHeadersFactory {

    @Inject
    Counter counter;

    @Override
    public MultivaluedMap<String, String> update(MultivaluedMap<String, String> inbound, MultivaluedMap<String, String> outbound) {

        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        headers.add("FOO", "BAR");
        headers.add("INJECTED_COUNT", counter != null ? Integer.toString(counter.count()) : "is_null");
        return headers;
    }
}
