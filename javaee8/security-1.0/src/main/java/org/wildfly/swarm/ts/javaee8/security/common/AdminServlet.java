package org.wildfly.swarm.ts.javaee8.security.common;

import javax.annotation.security.DeclareRoles;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin")
@DeclareRoles({"ADMIN", "USER"})
@ServletSecurity(@HttpConstraint(rolesAllowed = "ADMIN"))
public class AdminServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.getWriter().write("Successful access to admin servlet by " + req.getUserPrincipal().getName());
    }
}
