package org.wildfly.swarm.ts.hollow.jar.full;

import org.wildfly.swarm.ts.hollow.jar.full.jpa.Greeting;

import javax.annotation.Resource;
import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/jpa-2lc")
public class Jpa2ndLevelCacheServlet extends HttpServlet {
    @PersistenceContext
    private EntityManager em;

    @Resource
    private UserTransaction tx;

    @Resource
    private DataSource ds;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        String operation = req.getParameter("operation");

        try {
            if (operation == null) {
                StringBuilder result = new StringBuilder();
                for (Greeting greeting : em.createNamedQuery("Greeting.findAll", Greeting.class).getResultList()) {
                    result.append(greeting.getText()).append("\n");
                }
                resp.getWriter().print(result.toString());
                return;
            }

            switch (operation) {
                case "query": {
                    boolean directJdbc = Boolean.parseBoolean(req.getParameter("jdbc"));
                    long id = Long.parseLong(req.getParameter("id"));
                    if (directJdbc) {
                        try (Connection conn = ds.getConnection();
                             PreparedStatement stmt = conn.prepareStatement("SELECT text FROM Greeting WHERE id = ?")) {
                            stmt.setLong(1, id);
                            try (ResultSet rs = stmt.executeQuery()) {
                                rs.next();
                                resp.getWriter().print(rs.getString("text"));
                            }
                        }
                    } else {
                        resp.getWriter().print(em.find(Greeting.class, id).getText());
                    }
                    break;
                }
                case "create": {
                    String text = req.getParameter("text");
                    tx.begin();
                    Greeting greeting = new Greeting();
                    greeting.setText(text);
                    em.persist(greeting);
                    tx.commit();
                    resp.getWriter().print(greeting.getId());
                    break;
                }
                case "update": {
                    long id = Long.parseLong(req.getParameter("id"));
                    String newText = req.getParameter("text");
                    try (Connection conn = ds.getConnection();
                         PreparedStatement stmt = conn.prepareStatement("UPDATE Greeting  SET text = ? WHERE id = ?")) {
                        stmt.setString(1, newText);
                        stmt.setLong(2, id);
                        resp.getWriter().print(stmt.executeUpdate());
                    }
                    break;
                }
                case "delete": {
                    long id = Long.parseLong(req.getParameter("id"));
                    tx.begin();
                    Greeting greeting = em.find(Greeting.class, id);
                    em.remove(greeting);
                    tx.commit();
                    break;
                }
                case "cached": {
                    long id = Long.parseLong(req.getParameter("id"));
                    Cache cache = em.getEntityManagerFactory().getCache();
                    resp.getWriter().print("" + cache.contains(Greeting.class, id));
                    break;
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
