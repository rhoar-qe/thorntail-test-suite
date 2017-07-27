package org.wildfly.swarm.ts.javaee.naming;

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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            InitialContext ctx = new InitialContext();
            HelloBean hello = (HelloBean) ctx.lookup("java:module/HelloBean");
            resp.getWriter().print(hello.hello());
        } catch (NamingException e) {
            throw new ServletException(e);
        }
    }
}
