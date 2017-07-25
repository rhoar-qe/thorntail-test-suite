package org.wildfly.swarm.ts.javaee.el;

import javax.el.ELProcessor;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/")
public class HelloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Hello hello = new Hello("World");

        ELProcessor el = new ELProcessor();
        el.defineBean("hello", hello);
        String greeting = (String) el.eval("hello.greeting");

        resp.getWriter().print(greeting);
    }
}
