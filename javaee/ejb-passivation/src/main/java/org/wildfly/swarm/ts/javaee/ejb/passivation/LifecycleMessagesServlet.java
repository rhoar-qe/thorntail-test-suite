package org.wildfly.swarm.ts.javaee.ejb.passivation;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/lifecycle-messages")
public class LifecycleMessagesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HelloBean.lifecycleMessages.forEach(resp.getWriter()::println);
    }
}
