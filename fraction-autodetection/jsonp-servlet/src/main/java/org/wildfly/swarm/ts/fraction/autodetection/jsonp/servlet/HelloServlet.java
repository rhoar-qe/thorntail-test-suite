package org.wildfly.swarm.ts.fraction.autodetection.jsonp.servlet;

import java.io.IOException;

import javax.json.Json;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/")
public class HelloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        Json.createGenerator(resp.getWriter())
                .writeStartObject()
                .write("hello", "world")
                .writeEnd()
                .close();
    }
}
