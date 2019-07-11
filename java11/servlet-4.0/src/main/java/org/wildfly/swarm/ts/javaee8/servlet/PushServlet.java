package org.wildfly.swarm.ts.javaee8.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.PushBuilder;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/PushServlet")
public class PushServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter writer = resp.getWriter();
        writer.write("<html><head>");
        writer.write("<title>Servlet 4.0</title>");
        writer.write("<link rel=\"stylesheet\" href=\"css/style.css\"/>");
        writer.write("</head>");
        writer.write("<body>");
        writer.write("<p>" + (req.isSecure() ? "secure" : "insecure") + "</p>");
        PushBuilder pushBuilder = req.newPushBuilder();
        if (pushBuilder != null) {
            if (Boolean.parseBoolean(req.getParameter("forceFail"))) {
                try {
                    pushBuilder.push();
                } catch (IllegalStateException ex) {
                    writer.write("<p>pushing failed</p>");
                }
            } else {
                pushBuilder.path("css/style.css").push();
                writer.write("<p>pushing succeeded</p>");
            }
        } else {
            writer.write("<p>no pushing</p>");
        }
        writer.write("</body></html>");
    }
}
