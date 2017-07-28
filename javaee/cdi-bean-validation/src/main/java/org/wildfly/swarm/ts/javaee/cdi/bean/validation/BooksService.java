package org.wildfly.swarm.ts.javaee.cdi.bean.validation;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class BooksService {
    private AtomicLong idGenerator = new AtomicLong();

    private List<Book> books = new CopyOnWriteArrayList<>();

    public List<Book> list() {
        return books;
    }

    @Valid
    public Book create(
            @Isbn(message = "ISBN must be set to a valid value")
            String isbn,
            @NotNull(message = "Title must be set")
            String title,
            @NotNull(message = "Author must be set")
            String author
    ) {
        Book book = new Book(idGenerator.incrementAndGet(), isbn, title, author);
        books.add(book);
        return book;
    }
}
