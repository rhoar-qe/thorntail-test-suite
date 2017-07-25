package org.wildfly.swarm.ts.interceptors.cdi;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;

@Timed
@Interceptor
public class TimingInterceptor {
    @AroundInvoke
    public Object logInvocation(InvocationContext invocation) throws Exception {
        long start = System.currentTimeMillis();
        try {
            return invocation.proceed();
        } finally {
            long end = System.currentTimeMillis();
            Method method = invocation.getMethod();
            System.out.println(method.getDeclaringClass().getSimpleName() + "." + method.getName() + " took "
                    + (end - start) + " millis");
        }
    }
}
