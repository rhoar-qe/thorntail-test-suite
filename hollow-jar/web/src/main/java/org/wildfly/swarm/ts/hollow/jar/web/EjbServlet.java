package org.wildfly.swarm.ts.hollow.jar.web;

import org.wildfly.swarm.ts.hollow.jar.web.ejb.EjbLocalInterface;

import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/ejb")
public class EjbServlet extends HttpServlet {
    @EJB
    private EjbLocalInterface ejbLocal;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.getWriter().print(ejbLocal.method());
    }
}
