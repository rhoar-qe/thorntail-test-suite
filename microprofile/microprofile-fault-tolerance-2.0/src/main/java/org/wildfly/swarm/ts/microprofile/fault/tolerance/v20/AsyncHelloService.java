package org.wildfly.swarm.ts.microprofile.fault.tolerance.v20;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException;

@Asynchronous // all is async with fallback method
@ApplicationScoped
public class AsyncHelloService {
    @Timeout
    @Fallback(fallbackMethod = "processFallback")
    public CompletionStage<MyConnection> timeout(boolean fail) throws InterruptedException {
        if (fail) {
            Thread.sleep(2000);
        }
        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from @Timeout method";
            }
        });
    }

    @Bulkhead(value = 15, waitingTaskQueue = 15)
    @Timeout(value = 1000)
    @Fallback(fallbackMethod = "processFallback")
    public CompletionStage<MyConnection> bulkheadTimeout(boolean fail) throws InterruptedException {
        if (fail) {
            Thread.sleep(2000);
        }
        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from @Bulkhead @Timeout method";
            }
        });
    }

    @Bulkhead(value = 15, waitingTaskQueue = 5)
    @Timeout(value = 1, unit = ChronoUnit.SECONDS)
    @Fallback(fallbackMethod = "processFallback")
    public CompletionStage<MyConnection> bulkhead15q5Timeout(int counter) throws InterruptedException {
        if (counter % 2 == 1) {
            Thread.sleep(2000);
        }
        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from @Bulkhead @Timeout method" + counter;
            }
        });
    }

    @Bulkhead(value = 5, waitingTaskQueue = 5)
    @Timeout(value = 1, unit = ChronoUnit.SECONDS)
    @Fallback(fallbackMethod = "processFallback")
    public CompletionStage<MyConnection> bulkhead5q5Timeout(int counter) throws InterruptedException {
        if (counter % 2 == 1) {
            Thread.sleep(2000);
        }
        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from @Bulkhead @Timeout method" + counter;
            }
        });
    }

    @Bulkhead(value = 5, waitingTaskQueue = 5)
    @Timeout(value = 1, unit = ChronoUnit.SECONDS)
    @Retry(retryOn = TimeoutException.class)
    @Fallback(fallbackMethod = "processFallback")
    public CompletionStage<MyConnection> bulkhead5q5TimeoutRetry(int counter) throws InterruptedException {
        if (counter % 4 == 1) { // one of 4 triggers retry
            throw new TimeoutException("Simulated TimeoutException");
        } else if (counter % 4 == 0) { // one of 4 timeouts
            Thread.sleep(2000);
        }
        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from @Bulkhead @Timeout @Retry method" + counter;
            }
        });
    }

    @Bulkhead(value = 15, waitingTaskQueue = 5)
    @Timeout(value = 1, unit = ChronoUnit.SECONDS)
    @Retry(retryOn = TimeoutException.class)
    @Fallback(fallbackMethod = "processFallback")
    public CompletionStage<MyConnection> bulkhead15q5TimeoutRetry(int counter) throws InterruptedException {
        if (counter % 4 == 1) { // one of 4 triggers retry
            throw new TimeoutException("Simulated TimeoutException");
        } else if (counter % 4 == 0) { // one of 4 timeouts
            Thread.sleep(2000);
        }
        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from @Bulkhead @Timeout @Retry method" + counter;
            }
        });
    }

    @Bulkhead
    @Timeout
    @Retry
    @Fallback(fallbackMethod = "processFallback")
    public CompletionStage<MyConnection> bulkheadTimeoutRetry(boolean fail) throws InterruptedException {
        if (fail) {
            Thread.sleep(2000);
        }
        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from @Bulkhead @Timeout @Retry method";
            }
        });
    }

    @Retry(retryOn = IOException.class)
    @CircuitBreaker(failOn = IOException.class)
    @Fallback(fallbackMethod = "processFallback")
    public CompletionStage<MyConnection> retryCircuitBreaker(boolean fail) throws IOException {
        if (fail) {
            throw new IOException("Simulated IOException");
        }

        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from @Retry @CircuitBreaker method";
            }
        });
    }

    @Retry(retryOn = IOException.class)
    @CircuitBreaker(failOn = IOException.class, requestVolumeThreshold = 5,
            successThreshold = 3, delay = 2, delayUnit = ChronoUnit.SECONDS, failureRatio = 0.75)
    @Fallback(fallbackMethod = "processFallback")
    public CompletionStage<MyConnection> retryCircuitBreaker(int counter) throws IOException {
        if (counter % 4 != 0) { // 3/4 requests trigger IOException
            throw new IOException("Simulated IOException");
        }
        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from @Retry @CircuitBreaker method" + counter;
            }
        });
    }

    @Retry(maxRetries = 2)
    @CircuitBreaker(failOn = TimeoutException.class)
    @Timeout
    @Fallback(fallbackMethod = "processFallback")
    public CompletionStage<MyConnection> retryCircuitBreakerTimeout(boolean fail) {
        if (fail) {
            throw new TimeoutException("Simulated TimeoutException");
        }
        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from @Retry @CircuitBreaker @Timeout method";
            }
        });
    }

    @Retry(maxRetries = 2)
    @CircuitBreaker(failOn = TimeoutException.class)
    @Timeout
    @Fallback(fallbackMethod = "processFallback")
    public CompletionStage<MyConnection> retryCircuitBreakerTimeout(int counter) throws InterruptedException {
        if (counter % 4 == 1) { // one quarter requests are timeout-ed
            Thread.sleep(2000);
        }
        if (counter % 2 == 0) { // even request triggers timeout exception
            throw new TimeoutException("Simulated TimeoutException");
        }
        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from @Retry @CircuitBreaker @Timeout method" + counter;
            }
        });
    }

    @Retry
    @Timeout
    @Fallback(fallbackMethod = "processFallback")
    public CompletionStage<MyConnection> retryTimeout(boolean fail) throws InterruptedException {
        if (fail) {
            Thread.sleep(2000);
        }
        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from @Retry @Timeout method";
            }
        });
    }

    @Timeout
    @CircuitBreaker(failOn = TimeoutException.class)
    @Fallback(fallbackMethod = "processFallback")
    public CompletionStage<MyConnection> timeoutCircuitBreaker(boolean fail) {
        if (fail) {
            throw new TimeoutException("Simulated TimeoutException");
        }
        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from @Timeout @CircuitBreaker method";
            }
        });
    }

    private CompletionStage<MyConnection> processFallback(boolean fail) {
        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Fallback Hello";
            }
        });
    }

    private CompletionStage<MyConnection> processFallback(int counter) {
        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Fallback Hello" + counter;
            }
        });
    }
}
