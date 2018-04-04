package org.wildfly.swarm.ts.hollow.jar.full;

import org.xadisk.connector.outbound.XADiskConnection;
import org.xadisk.connector.outbound.XADiskConnectionFactory;

import javax.annotation.Resource;
import javax.resource.ResourceException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/ra")
public class ResourceAdapterServlet extends HttpServlet {
    @Resource(lookup = "java:global/XADiskCF")
    private XADiskConnectionFactory xaConnectionFactory;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (xaConnectionFactory != null) {
            XADiskConnection connection = null;
            try {
                connection = xaConnectionFactory.getConnection();
                if (connection != null) {
                    resp.getWriter().print("OK");
                }
            } catch (ResourceException e) {
                throw new ServletException(e);
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        }
    }
}
