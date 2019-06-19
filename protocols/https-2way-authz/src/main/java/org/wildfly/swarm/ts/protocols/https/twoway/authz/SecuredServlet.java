package org.wildfly.swarm.ts.protocols.https.twoway.authz;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.cert.X509Certificate;

@WebServlet("/secured")
public class SecuredServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        resp.getWriter().print("Client certificate: " + getClientCertificate(req).getSubjectX500Principal().getName());
    }

    private static X509Certificate getClientCertificate(HttpServletRequest req) throws ServletException {
        X509Certificate[] certs = (X509Certificate[]) req.getAttribute("javax.servlet.request.X509Certificate");
        if (certs != null && certs.length > 0) {
            return certs[0];
        }
        throw new ServletException("Client certificate missing");
    }
}
