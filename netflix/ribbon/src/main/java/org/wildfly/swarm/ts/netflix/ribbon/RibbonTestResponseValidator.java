package org.wildfly.swarm.ts.netflix.ribbon;

import com.netflix.ribbon.ServerError;
import com.netflix.ribbon.UnsuccessfulResponseException;
import com.netflix.ribbon.http.HttpResponseValidator;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;

public class RibbonTestResponseValidator implements HttpResponseValidator {

    @Override
    public void validate(HttpClientResponse<ByteBuf> response) throws UnsuccessfulResponseException, ServerError {
        if (response.getStatus().code() == 404) {
            throw new ServerError("Http 404 found");
        }
    }

}
