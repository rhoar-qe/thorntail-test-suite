package org.wildfly.swarm.ts.javaee.servlet.ejb;

import java.io.IOException;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/hello")
public class GreeterServlet extends HttpServlet {
    @EJB
    private SimpleService simpleService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        GreeterService greeter = (GreeterService) session.getAttribute(GreeterService.class.getName());
        if (greeter == null) {
            try {
                greeter = InitialContext.doLookup("java:module/" + GreeterService.class.getSimpleName());
                session.setAttribute(GreeterService.class.getName(), greeter);
            } catch (NamingException e) {
                throw new ServletException(e);
            }
        }

        String name = req.getParameter("name");
        resp.getWriter().print(simpleService.yay() + greeter.hello(name));
    }
}
