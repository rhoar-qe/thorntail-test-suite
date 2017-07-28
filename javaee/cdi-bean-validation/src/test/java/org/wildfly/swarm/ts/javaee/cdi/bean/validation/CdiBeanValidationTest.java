package org.wildfly.swarm.ts.javaee.cdi.bean.validation;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Can't use AssertJ because this is an in-container test with a JAR deployment. That means dependencies aren't added
 * to the deployment neither by Swarm nor by Arquillian.
 */
@RunWith(Arquillian.class)
@DefaultDeployment(type = DefaultDeployment.Type.JAR)
public class CdiBeanValidationTest {
    @Inject
    private BooksService books;

    @Test
    @InSequence(1)
    public void empty() {
        assertTrue(books.list().isEmpty());
    }

    @Test
    @InSequence(2)
    public void putInvalid() {
        try {
            books.create("bad ISBN", "Title", "Author");
            fail("Validation should fail");
        } catch (ConstraintViolationException e) {
            assertThat(e.getMessage(), containsString("ISBN must be valid"));
        }

        try {
            books.create(null, "Title", "Author");
            fail("Validation should fail");
        } catch (ConstraintViolationException e) {
            assertThat(e.getMessage(), containsString("ISBN must be set"));
        }

        assertTrue(books.list().isEmpty());
    }

    @Test
    @InSequence(3)
    public void putValid() {
        Book book = books.create("1234567890", "Title", "Author");
        assertEquals(1, book.getId());
        assertEquals("1234567890", book.getIsbn());
        assertEquals(1, books.list().size());
    }
}
