package org.wildfly.swarm.ts.javaee.interceptors.ejb;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;

public class LoggingInterceptor {
    @AroundInvoke
    public Object logInvocation(InvocationContext invocation) throws Exception {
        Method method = invocation.getMethod();
        System.out.println(method.getDeclaringClass().getSimpleName() + "." + method.getName() + " invoked");
        return invocation.proceed();
    }
}
