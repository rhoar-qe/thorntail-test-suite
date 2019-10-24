package org.wildfly.swarm.ts.javaee8.cdi.interceptionfactory;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor
@CustomInterceptorBinding
@Priority(1)
public class MyInterceptor {
    @AroundInvoke
    public Object addParam(InvocationContext context) throws Exception {
        if (context.getMethod().getName().equals("setGreeting")) {
            context.setParameters(new Object[]{"Param: " + context.getParameters()[0]});
        }
        return context.proceed();
    }

}
