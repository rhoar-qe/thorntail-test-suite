package org.wildfly.swarm.ts.hollow.jar.full;

import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.wildfly.swarm.ts.hollow.jar.full.ejb.passivation.EjbPassivationBean;

import java.io.IOException;

@WebServlet("/ejbpassivation")
public class EjbPassivationServlet extends HttpServlet {
    @Inject
    private Result result;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String operation = req.getParameter("operation");

        if (operation == null) {
            try {
                EjbPassivationBean ejb = (EjbPassivationBean) new InitialContext().lookup("java:module/EjbPassivationBean");
                resp.getWriter().print(ejb.hello());
            } catch (NamingException e) {
                throw new ServletException(e);
            }
        } else {
            result.getItems().forEach(resp.getWriter()::println);
        }
    }
}
