package org.wildfly.swarm.ts.javaee.cdi.bean.validation;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

@RunWith(Arquillian.class)
@DefaultDeployment(type = DefaultDeployment.Type.JAR)
public class CdiBeanValidationTest {
    @Inject
    private BooksService books;

    @Test
    @InSequence(1)
    public void empty() {
        assertThat(books.list()).isEmpty();
    }

    @Test
    @InSequence(2)
    public void putInvalid() {
        try {
            books.create("bad ISBN", "Title", "Author");
            fail("Validation should fail");
        } catch (ConstraintViolationException e) {
            assertThat(e.getMessage()).contains("ISBN must be valid");
        }

        try {
            books.create(null, "Title", "Author");
            fail("Validation should fail");
        } catch (ConstraintViolationException e) {
            assertThat(e.getMessage()).contains("ISBN must be set");
        }

        assertThat(books.list()).isEmpty();
    }

    @Test
    @InSequence(3)
    public void putValid() {
        Book book = books.create("1234567890", "Title", "Author");
        assertThat(book.getId()).isEqualTo(1);
        assertThat(book.getIsbn()).isEqualTo("1234567890");
        assertThat(books.list()).hasSize(1);
    }
}
