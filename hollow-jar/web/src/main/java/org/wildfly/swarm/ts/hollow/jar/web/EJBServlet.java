package org.wildfly.swarm.ts.hollow.jar.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.wildfly.swarm.ts.hollow.jar.web.ejb.EJBLocalInterface;

@WebServlet (urlPatterns = "/ejb")
public class EJBServlet extends HttpServlet{

    @EJB
    private EJBLocalInterface ejbLocal;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        writer.print(ejbLocal.method());
    }
}
