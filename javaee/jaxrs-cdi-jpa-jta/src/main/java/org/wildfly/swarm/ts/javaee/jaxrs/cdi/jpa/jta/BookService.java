package org.wildfly.swarm.ts.javaee.jaxrs.cdi.jpa.jta;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
@Transactional
public class BookService {
    @PersistenceContext(unitName = "MyPU")
    private EntityManager em;

    public List<Book> getAll() {
        return em.createNamedQuery("Book.getAll", Book.class).getResultList();
    }

    public Book get(int id) {
        return em.find(Book.class, id);
    }

    public Book create(String title, String author) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        em.persist(book);
        return book;
    }

    public void delete(int id) {
        Book book = em.find(Book.class, id);
        em.remove(book);
    }
}
