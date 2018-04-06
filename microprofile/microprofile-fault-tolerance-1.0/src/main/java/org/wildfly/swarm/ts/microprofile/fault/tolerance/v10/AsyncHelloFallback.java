package org.wildfly.swarm.ts.microprofile.fault.tolerance.v10;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;

import java.util.concurrent.Future;

import static java.util.concurrent.CompletableFuture.completedFuture;

public class AsyncHelloFallback implements FallbackHandler<Future<String>> {
    @Override
    public Future<String> handle(ExecutionContext context) {
        return completedFuture("Fallback Hello");
    }
}
