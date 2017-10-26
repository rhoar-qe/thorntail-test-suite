package org.wildfly.swarm.ts.netflix.ribbon.secured;

import com.netflix.ribbon.RibbonRequest;
import com.netflix.ribbon.proxy.annotation.Http;
import com.netflix.ribbon.proxy.annotation.Http.HttpMethod;
import com.netflix.ribbon.proxy.annotation.Hystrix;
import com.netflix.ribbon.proxy.annotation.ResourceGroup;
import io.netty.buffer.ByteBuf;
import org.wildfly.swarm.netflix.ribbon.secured.client.SecuredRibbon;

@ResourceGroup(name = "ts-netflix-ribbon-secured")
public interface SecuredRibbonClient {
    SecuredRibbonClient INSTANCE = SecuredRibbon.from(SecuredRibbonClient.class);

    @Http(method = HttpMethod.GET, uri = "/hello")
    @Hystrix(fallbackHandler = RibbonTestFallbackHandler.class)
    RibbonRequest<ByteBuf> ribbonTest();
}
