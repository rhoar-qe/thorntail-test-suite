package org.wildfly.swarm.ts.javaee8.jpa;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@WebServlet("/")
public class MyServlet extends HttpServlet {
    @PersistenceContext
    private EntityManager em;

    @Resource
    private UserTransaction tx;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String response = em.createNamedQuery("MyEntity.findAllIds", Long.class)
                .getResultStream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        resp.getWriter().write(response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            tx.begin();
            MyEntity entity = new MyEntity();
            LocalDateTime dateTime = getDateTime(req);
            entity.setLocalDateTime(dateTime);
            entity.setMyAttribute(new MyAttribute("f", "l"));
            em.persist(entity);
            tx.commit();
        } catch (Exception e) {
            throw new ServletException(e);
        }

        PrintWriter writer = resp.getWriter();
        em.createNamedQuery("MyEntity.findAll", MyEntity.class)
                .getResultStream()
                .forEachOrdered(writer::println);
    }

    private static LocalDateTime getDateTime(HttpServletRequest req) {
        int year = parseOrDefault(req, "year", 0);
        int month = parseOrDefault(req, "month", 1);
        int day = parseOrDefault(req, "day", 1);
        int hour = parseOrDefault(req, "hour", 0);
        int minute = parseOrDefault(req, "minute", 0);
        return LocalDate.of(year, month, day).atTime(hour, minute);
    }

    private static int parseOrDefault(HttpServletRequest req, String parameter, int defaultValue) {
        try {
            return Integer.parseInt(req.getParameter(parameter));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
