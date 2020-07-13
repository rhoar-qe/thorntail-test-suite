package org.wildfly.swarm.ts.microprofile.fault.tolerance.v21.timeout;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.wildfly.swarm.ts.microprofile.fault.tolerance.v21.MyConnection;

@ApplicationScoped
public class TimeoutService {
    @Inject
    private TimeoutContext context;

    @Timeout
    @Fallback(TimeoutFallback.class)
    public String timeout(boolean fail) throws InterruptedException {
        if (fail) {
            Thread.sleep(2000);
        }
        return "Hello from @Timeout method, context = " + context.getValue();
    }

    @Asynchronous
    @Timeout
    @Fallback(TimeoutAsyncFallback.class)
    public CompletionStage<MyConnection> timeoutAsync(boolean fail) throws InterruptedException {
        if (fail) {
            Thread.sleep(2000);
        }
        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Async Hello from @Timeout method, context = " + context.getValue();
            }
        });
    }
}
