package org.wildfly.swarm.ts.microprofile.fault.tolerance.v21.fallback;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.wildfly.swarm.ts.microprofile.fault.tolerance.v21.MyConnection;
import org.wildfly.swarm.ts.microprofile.fault.tolerance.v21.MyException;

@Asynchronous // all is async
@ApplicationScoped
public class FallbackService {

    public CompletionStage<MyConnection> exception(boolean fail) throws Exception {
        if (fail) {
            throw new Exception("Simulated Exception");
        }

        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from exception";
            }
        });
    }

    public CompletionStage<MyConnection> myException(boolean fail) throws Exception {
        if (fail) {
            throw new MyException("Simulated MyException");
        }

        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from myException";
            }
        });
    }

    @Fallback(fallbackMethod = "processFallback", skipOn = MyException.class)
    public CompletionStage<MyConnection> exception_skipOnMyE(boolean fail) throws Exception {
        if (fail) {
            throw new Exception("Simulated Exception");
        }

        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from exception_skipOnMyE";
            }
        });
    }

    @Fallback(fallbackMethod = "processFallback", applyOn = Exception.class, skipOn = MyException.class)
    public CompletionStage<MyConnection> exception_applyOnE_skipOnMyE(boolean fail) throws Exception {
        if (fail) {
            throw new Exception("Simulated Exception");
        }

        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from exception_applyOnE_skipOnMyE";
            }
        });
    }

    @Fallback(fallbackMethod = "processFallback", applyOn = Exception.class, skipOn = MyException.class)
    public CompletionStage<MyConnection> myException_applyOnE_skipOnMyE(boolean fail) throws Exception {
        if (fail) {
            throw new MyException("Simulated MyException");
        }

        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from myException_applyOnE_skipOnMyE";
            }
        });
    }

    @Fallback(fallbackMethod = "processFallback", applyOn = MyException.class, skipOn = Exception.class)
    public CompletionStage<MyConnection> exception_applyOnMyE_skipOnE(boolean fail) throws Exception {
        if (fail) {
            throw new Exception("Simulated Exception");
        }

        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from exception_applyOnMyE_skipOnE";
            }
        });
    }

    @Fallback(fallbackMethod = "processFallback", applyOn = MyException.class, skipOn = Exception.class)
    public CompletionStage<MyConnection> myException_applyOnMyE_skipOnE(boolean fail) throws Exception {
        if (fail) {
            throw new MyException("Simulated MyException");
        }

        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from myException_applyOnMyE_skipOnE";
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
}