package org.wildfly.swarm.ts.servlet.jpa.jta;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;

@WebServlet("/")
public class BooksServlet extends HttpServlet {
    @PersistenceUnit(unitName = "MyPU")
    private EntityManagerFactory emf;

    @Resource
    private UserTransaction tx;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EntityManager em = emf.createEntityManager();
        try {
            List<Book> books = em.createNamedQuery("Book.getAll", Book.class).getResultList();
            for (Book book : books) {
                resp.getWriter().println(book.getId() + " " + book.getAuthor() + ": " + book.getTitle());
            }
        } finally {
            em.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String title = req.getParameter("title");
        String author = req.getParameter("author");

        EntityManager em = emf.createEntityManager();
        try {
            tx.begin();
            Book book = new Book();
            book.setTitle(title);
            book.setAuthor(author);
            em.persist(book);
            tx.commit();

            resp.getWriter().print(book.getId());
        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            em.close();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));

        EntityManager em = emf.createEntityManager();
        try {
            tx.begin();
            Book book = em.find(Book.class, id);
            em.remove(book);
            tx.commit();

            resp.getWriter().print(id + " deleted");
        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            em.close();
        }
    }
}
