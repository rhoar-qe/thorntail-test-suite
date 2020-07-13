package org.wildfly.swarm.ts.microprofile.fault.tolerance.v21.timeout;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;
import org.wildfly.swarm.ts.microprofile.fault.tolerance.v21.MyConnection;

public class TimeoutAsyncFallback implements FallbackHandler<CompletionStage<MyConnection>> {

    @Inject
    private TimeoutContext context;

    @Override
    public CompletionStage<MyConnection> handle(ExecutionContext c) {

        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Async Fallback Hello, context = " + context.getValue();
            }
        });
    }
}

