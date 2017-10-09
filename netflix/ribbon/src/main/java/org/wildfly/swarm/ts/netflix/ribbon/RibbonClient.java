package org.wildfly.swarm.ts.netflix.ribbon;

import com.netflix.ribbon.Ribbon;
import com.netflix.ribbon.RibbonRequest;
import com.netflix.ribbon.proxy.annotation.Http;
import com.netflix.ribbon.proxy.annotation.Hystrix;
import com.netflix.ribbon.proxy.annotation.ResourceGroup;
import com.netflix.ribbon.proxy.annotation.TemplateName;

import io.netty.buffer.ByteBuf;

@ResourceGroup( name="ribbonTest" )
public interface RibbonClient {

    RibbonClient INSTANCE = Ribbon.from(RibbonClient.class);

    @TemplateName("ribbonTest")
    @Http(
            method = Http.HttpMethod.GET,
            uri = "http://localhost:8080/"
    )
    @Hystrix(
            fallbackHandler = RibbonTestFallbackHandler.class,
            validator = RibbonTestResponseValidator.class
    )
    RibbonRequest<ByteBuf> ribbonTest();

}
