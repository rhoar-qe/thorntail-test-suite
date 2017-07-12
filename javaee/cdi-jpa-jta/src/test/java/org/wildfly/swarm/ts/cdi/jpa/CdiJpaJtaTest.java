package org.wildfly.swarm.ts.cdi.jpa;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import javax.inject.Inject;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Can't use AssertJ because this is an in-container test with a JAR deployment. That means dependencies aren't added
 * to the deployment neither by Swarm nor by Arquillian.
 */
@RunWith(Arquillian.class)
@DefaultDeployment(type = DefaultDeployment.Type.JAR)
public class CdiJpaJtaTest {
    private static int id = -1;

    @Inject
    private BookService books;

    @Inject
    private UserTransaction tx;

    @Test
    @InSequence(1)
    public void empty() {
        assertTrue(books.getAll().isEmpty());
    }

    @Test
    @InSequence(2)
    public void create() {
        Book book = books.create("Title", "Author");
        assertNotNull(book);
        id = book.getId();
    }

    @Test
    @InSequence(3)
    public void notEmpty() {
        assertEquals(1, books.getAll().size());
    }

    @Test
    @InSequence(4)
    public void delete() {
        books.delete(id);
    }

    @Test
    @InSequence(5)
    public void emptyAgain() {
        assertTrue(books.getAll().isEmpty());
    }

    @Test
    @InSequence(6)
    public void userTransaction() throws SystemException {
        assertNotNull(tx);
        assertEquals(Status.STATUS_NO_TRANSACTION, tx.getStatus());
    }
}
