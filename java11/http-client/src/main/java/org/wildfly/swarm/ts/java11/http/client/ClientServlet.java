package org.wildfly.swarm.ts.java11.http.client;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@WebServlet("/client")
public class ClientServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/hello")).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            resp.getWriter().print("New Java HTTP Client got: " + response.body());
        } catch (InterruptedException e) {
            throw new ServletException(e);
        }
    }
}
