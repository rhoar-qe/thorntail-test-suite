package org.wildfly.swarm.ts.javaee.jpa2lc;

import javax.annotation.Resource;
import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Path("/")
public class GreetingResource {
    @PersistenceContext
    private EntityManager em;

    @Resource
    private UserTransaction tx;

    @Resource
    private DataSource ds;

    @GET
    public String getAll() {
        StringBuilder result = new StringBuilder();
        for (Greeting greeting : em.createNamedQuery("Greeting.findAll", Greeting.class).getResultList()) {
            result.append(greeting.getText()).append("\n");
        }
        return result.toString();
    }

    @GET
    @Path("/{id}")
    public String get(@PathParam("id") long id, @QueryParam("jdbc") boolean directJdbc) throws SQLException {
        if (directJdbc) {
            try (Connection conn = ds.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT text FROM Greeting WHERE id = ?")) {
                stmt.setLong(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    rs.next();
                    return rs.getString("text");
                }
            }
        } else {
            return em.find(Greeting.class, id).getText();
        }
    }

    @POST
    public long create(@QueryParam("text") String text) throws Exception {
        tx.begin();
        Greeting greeting = new Greeting();
        greeting.setText(text);
        em.persist(greeting);
        tx.commit();
        return greeting.getId();
    }

    @POST
    @Path("/{id}")
    public int updateViaDirectJdbc(@PathParam("id") long id, @QueryParam("text") String newText) throws SQLException {
        try (Connection conn = ds.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE Greeting  SET text = ? WHERE id = ?")) {
            stmt.setString(1, newText);
            stmt.setLong(2, id);
            return stmt.executeUpdate();
        }
    }

    @DELETE
    @Path("/{id}")
    public void delete(@PathParam("id") long id) throws Exception {
        tx.begin();
        Greeting greeting = em.find(Greeting.class, id);
        em.remove(greeting);
        tx.commit();
    }

    @GET
    @Path("/{id}/cached")
    public String isCached(@PathParam("id") long id) {
        Cache cache = em.getEntityManagerFactory().getCache();
        return "" + cache.contains(Greeting.class, id);
    }
}
