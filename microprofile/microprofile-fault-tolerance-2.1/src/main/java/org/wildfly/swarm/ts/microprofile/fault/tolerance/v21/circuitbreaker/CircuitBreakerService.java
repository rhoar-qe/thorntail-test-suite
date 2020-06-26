package org.wildfly.swarm.ts.microprofile.fault.tolerance.v21.circuitbreaker;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.wildfly.swarm.ts.microprofile.fault.tolerance.v21.MyConnection;
import org.wildfly.swarm.ts.microprofile.fault.tolerance.v21.MyException;

@Asynchronous // all is async
@ApplicationScoped
public class CircuitBreakerService {

    @CircuitBreaker(failOn = Exception.class)
    @Fallback(fallbackMethod = "processFallback")
    public CompletionStage<MyConnection> exception_failOnE(boolean fail) throws Exception {
        if (fail) {
            throw new Exception("Simulated Exception");
        }

        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from exception_failOnE";
            }
        });
    }

    @CircuitBreaker(failOn = MyException.class)
    @Fallback(fallbackMethod = "processFallback")
    public CompletionStage<MyConnection> exception_failOnMyE(boolean fail) throws Exception {
        if (fail) {
            throw new Exception("Simulated Exception");
        }

        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from exception_failOnMyE";
            }
        });
    }

    @CircuitBreaker(failOn = Exception.class)
    @Fallback(fallbackMethod = "processFallback")
    public CompletionStage<MyConnection> myException_failOnE(boolean fail) throws Exception {
        if (fail) {
            throw new MyException("Simulated MyException");
        }

        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from myException_failOnE";
            }
        });
    }

    @CircuitBreaker(failOn = MyException.class)
    @Fallback(fallbackMethod = "processFallback")
    public CompletionStage<MyConnection> myException_failOnMyE(boolean fail) throws Exception {
        if (fail) {
            throw new MyException("Simulated MyException");
        }

        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from myException_failOnMyE";
            }
        });
    }

    @Fallback(fallbackMethod = "processFallback")
    public CompletionStage<MyConnection> myException_noCB(boolean fail) throws Exception {
        if (fail) {
            throw new MyException("Simulated MyException");
        }

        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from myException_noCB";
            }
        });
    }

    @CircuitBreaker
    @Fallback(fallbackMethod = "processFallback")
    public CompletionStage<MyConnection> myException_blankCB(boolean fail) throws Exception {
        if (fail) {
            throw new MyException("Simulated MyException");
        }

        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from myException_blankCB";
            }
        });
    }

    @CircuitBreaker(failOn = Exception.class, skipOn = MyException.class)
    @Fallback(fallbackMethod = "processFallback")
    public CompletionStage<MyConnection> exception_failOnE_skipOnMyE(boolean fail) throws Exception {
        if (fail) {
            throw new Exception("Simulated Exception");
        }

        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from exception_failOnE_skipOnMyE";
            }
        });
    }

    @CircuitBreaker(failOn = Exception.class, skipOn = MyException.class)
    @Fallback(fallbackMethod = "processFallback")
    public CompletionStage<MyConnection> myException_failOnE_skipOnMyE(boolean fail) throws Exception {
        if (fail) {
            throw new MyException("Simulated MyException");
        }

        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from myException_failOnE_skipOnMyE";
            }
        });
    }

    @CircuitBreaker(skipOn = MyException.class)
    @Fallback(fallbackMethod = "processFallback")
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

    @CircuitBreaker(skipOn = MyException.class)
    @Fallback(fallbackMethod = "processFallback")
    public CompletionStage<MyConnection> myException_skipOnMyE(boolean fail) throws Exception {
        if (fail) {
            throw new MyException("Simulated MyException");
        }

        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from myException_skipOnMyE";
            }
        });
    }

    @CircuitBreaker(failOn = MyException.class, skipOn = Exception.class)
    @Fallback(fallbackMethod = "processFallback")
    public CompletionStage<MyConnection> exception_failOnMyE_skipOnE(boolean fail) throws Exception {
        if (fail) {
            throw new Exception("Simulated Exception");
        }

        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from exception_failOnMyE_skipOnE";
            }
        });
    }

    @CircuitBreaker(failOn = MyException.class, skipOn = Exception.class)
    @Fallback(fallbackMethod = "processFallback")
    public CompletionStage<MyConnection> myException_failOnMyE_skipOnE(boolean fail) throws Exception {
        if (fail) {
            throw new MyException("Simulated MyException");
        }

        return CompletableFuture.completedFuture(new MyConnection() {
            @Override
            public String getData() {
                return "Hello from myException_failOnMyE_skipOnE";
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