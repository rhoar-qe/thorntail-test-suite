package org.wildfly.swarm.ts.microprofile.fault.tolerance.v21.retry;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.context.RequestScoped;

import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.wildfly.swarm.ts.microprofile.fault.tolerance.v21.MyConnection;
import org.wildfly.swarm.ts.microprofile.fault.tolerance.v21.MyException;

@Asynchronous // all is async with fallback method
@RequestScoped
public class RetryService {

    private AtomicInteger counter = new AtomicInteger(0);

    // First fail, second pass - there will be 1 retry before success
    @Retry(retryOn = Throwable.class)
    public CompletionStage<MyConnection> throwable_retryOnT() throws Throwable {
        int invocationNumber = counter.incrementAndGet();
        if (invocationNumber % 2 == 1) {
            throw new Throwable("Simulated Throwable");
        }

        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from throwable_retryOnT, counter: " + invocationNumber;
            }
        });
    }

    // First fail, second pass - there will be 1 retry before success (Throwable is parent of Exception)
    @Retry(retryOn = Throwable.class)
    @Fallback(fallbackMethod = "processFallback")
    public CompletionStage<MyConnection> myException_retryOnT() throws MyException {
        int invocationNumber = counter.incrementAndGet();
        if (invocationNumber % 2 == 1) {
            throw new MyException("Simulated custom Exception");
        }

        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from myException_retryOnT, counter: " + invocationNumber;
            }
        });
    }

    // First call goes through and is recognized by fallback
    @Retry(abortOn = Throwable.class)
    @Fallback(fallbackMethod = "processFallback")
    public CompletionStage<MyConnection> throwable_abortOnT() throws Throwable {
        int invocationNumber = counter.incrementAndGet();
        if (invocationNumber % 2 == 1) {
            throw new Throwable("Simulated Throwable");
        }

        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from throwable_abortOnT, counter: " + invocationNumber;
            }
        });
    }

    // First call goes through and is recognized by fallback (Throwable is parent of Exception)
    @Retry(abortOn = Throwable.class)
    @Fallback(fallbackMethod = "processFallback")
    public CompletionStage<MyConnection> myException_abortOnT() throws MyException {
        int invocationNumber = counter.incrementAndGet();
        if (invocationNumber % 2 == 1) {
            throw new MyException("Simulated custom Exception");
        }

        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from myException_abortOnT, counter: " + invocationNumber;
            }
        });
    }

    // First call is Throwable = fallback
    @Retry(retryOn = MyException.class, abortOn = Throwable.class)
    @Fallback(fallbackMethod = "processFallback")
    public CompletionStage<MyConnection> throwable_retryOnMyE_abortOnT() throws Throwable {
        int invocationNumber = counter.incrementAndGet();
        if (invocationNumber % 2 == 1) {
            throw new Throwable("Simulated Throwable");
        }

        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from throwable_retryOnMyE_abortOnT, counter: " + invocationNumber;
            }
        });
    }

    // First fail, second pass - there will be 1 retry before success
    @Retry(retryOn = Throwable.class, abortOn = MyException.class)
    @Fallback(fallbackMethod = "processFallback")
    public CompletionStage<MyConnection> throwable_retryOnT_abortOnMyE() throws Throwable {
        int invocationNumber = counter.incrementAndGet();
        if (invocationNumber % 2 == 1) {
            throw new Throwable("Simulated Throwable");
        }

        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from throwable_retryOnT_abortOnMyE, counter: " + invocationNumber;
            }
        });
    }

    // First call goes through and is recognized by fallback (Throwable is parent of Exception)
    @Retry(retryOn = MyException.class, abortOn = Throwable.class)
    @Fallback(fallbackMethod = "processFallback")
    public CompletionStage<MyConnection> myException_retryOnMyE_abortOnT() throws MyException {
        int invocationNumber = counter.incrementAndGet();
        if (invocationNumber % 2 == 1) {
            throw new MyException("Simulated Exception");
        }

        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from myException_retryOnMyE_abortOnT, counter: " + invocationNumber;
            }
        });
    }

    // First call goes through and is recognized by fallback(abortOn has higher priority than retryOn)
    @Retry(retryOn = Throwable.class, abortOn = MyException.class)
    @Fallback(fallbackMethod = "processFallback")
    public CompletionStage<MyConnection> myException_retryOnT_abortOnMyE() throws MyException {
        int invocationNumber = counter.incrementAndGet();
        if (invocationNumber % 2 == 1) {
            throw new MyException("Simulated Exception");
        }

        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from myException_retryOnT_abortOnMyE, counter: " + invocationNumber;
            }
        });
    }

    private CompletionStage<MyConnection> processFallback() {
        int invocationNumber = counter.get();
        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from fallback, counter: " + invocationNumber;
            }
        });
    }
}