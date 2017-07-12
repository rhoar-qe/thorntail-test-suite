package org.wildfly.swarm.ts.cdi.jpa;

import javax.persistence.*;

@Entity
@NamedQueries({
        @NamedQuery(name = "Book.getAll", query = "SELECT b FROM Book b")
})
public class Book {
    @Id
    @GeneratedValue
    private int id;

    private String title;

    private String author;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
