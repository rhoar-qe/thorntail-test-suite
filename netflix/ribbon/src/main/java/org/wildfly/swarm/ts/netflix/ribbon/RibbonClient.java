package org.wildfly.swarm.ts.netflix.ribbon;

import com.netflix.ribbon.Ribbon;
import com.netflix.ribbon.RibbonRequest;
import com.netflix.ribbon.proxy.annotation.Http;
import com.netflix.ribbon.proxy.annotation.Http.HttpMethod;
import com.netflix.ribbon.proxy.annotation.Hystrix;
import com.netflix.ribbon.proxy.annotation.ResourceGroup;
import io.netty.buffer.ByteBuf;

@ResourceGroup(name = "ts-netflix-ribbon")
public interface RibbonClient {
    RibbonClient INSTANCE = Ribbon.from(RibbonClient.class);

    @Http(method = HttpMethod.GET, uri = "/hello")
    @Hystrix(fallbackHandler = RibbonTestFallbackHandler.class)
    RibbonRequest<ByteBuf> ribbonTest();
}
