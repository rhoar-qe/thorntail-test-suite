package org.wildfly.swarm.ts.javaee.cdi.bean.validation;

import javax.validation.constraints.NotNull;

public class Book {
    private long id;

    @Isbn(message = "ISBN must be set to a valid value")
    private String isbn;

    @NotNull(message = "Title must be set")
    private String title;

    @NotNull(message = "Author must be set")
    private String author;

    public Book(long id, String isbn, String title, String author) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
    }

    public long getId() {
        return id;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }
}
