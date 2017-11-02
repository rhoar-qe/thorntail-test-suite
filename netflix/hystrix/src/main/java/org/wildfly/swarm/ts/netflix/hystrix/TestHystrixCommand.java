package org.wildfly.swarm.ts.netflix.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class TestHystrixCommand extends HystrixCommand<String> {

    private boolean throwError;

    public TestHystrixCommand(boolean throwError) {
        super(HystrixCommandGroupKey.Factory.asKey("TSHystrixGroup"));
        this.throwError = throwError;
    }

    @Override
    protected String run() throws Exception {
        if (throwError) {
            throw new RuntimeException();
        } else {
            return "OK";
        }
    }

    @Override
    protected String getFallback() {
        return "Fallback string";
    }
}
