package org.wildfly.swarm.ts.javaee.ejb.passivation;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/")
public class HelloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try {
            HelloBean ejb = (HelloBean) new InitialContext().lookup("java:module/HelloBean");
            resp.getWriter().print(ejb.hello());
        } catch (NamingException e) {
            throw new ServletException(e);
        }
    }
}
