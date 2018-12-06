package org.wildfly.swarm.ts.javaee.cdi.jpa;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import javax.inject.Inject;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(books.getAll()).isEmpty();
    }

    @Test
    @InSequence(2)
    public void create() {
        Book book = books.create("Title", "Author");
        assertThat(book).isNotNull();
        id = book.getId();
    }

    @Test
    @InSequence(3)
    public void notEmpty() {
        assertThat(books.getAll()).hasSize(1);
    }

    @Test
    @InSequence(4)
    public void delete() {
        books.delete(id);
    }

    @Test
    @InSequence(5)
    public void emptyAgain() {
        assertThat(books.getAll()).isEmpty();
    }

    @Test
    @InSequence(6)
    public void userTransaction() throws SystemException {
        assertThat(tx).isNotNull();
        assertThat(tx.getStatus()).isEqualTo(Status.STATUS_NO_TRANSACTION);
    }
}
