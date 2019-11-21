package org.wildfly.swarm.ts.javaee8.security.auth.form;

import javax.inject.Inject;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.SecurityContext;
import javax.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {
    @Inject
    private SecurityContext securityContext;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        AuthenticationStatus status = securityContext.authenticate(req, resp,
                AuthenticationParameters.withParams().credential(new UsernamePasswordCredential(email, password)));
        switch (status) {
            case SUCCESS:
                resp.getWriter().write("Authentication: success");
                break;
            case SEND_CONTINUE:
                resp.getWriter().write("Authentication: send_continue");
                break;
            case SEND_FAILURE:
                resp.getWriter().write("Authentication: send_failure");
                break;
            case NOT_DONE:
                resp.getWriter().write("Authentication: not_done");
                break;
        }
    }
}
