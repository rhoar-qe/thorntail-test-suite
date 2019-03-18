package org.wildfly.swarm.ts.javaee.jpa;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.util.Objects;

@Entity
@NamedQueries({
        @NamedQuery(name = "Greeting.findAll", query = "select g from Greeting g")
})
public class Greeting {
    @Id
    @GeneratedValue
    private long id;

    @Basic
    private String text;

    public Greeting() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return id + ": " + text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Greeting)) return false;
        Greeting greeting = (Greeting) o;
        return id == greeting.id && Objects.equals(text, greeting.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text);
    }
}
