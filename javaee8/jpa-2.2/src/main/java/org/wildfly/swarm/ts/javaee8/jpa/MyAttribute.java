package org.wildfly.swarm.ts.javaee8.jpa;

import java.util.Objects;

public class MyAttribute {
    private String first;
    private String last;

    public MyAttribute(String first, String last) {
        setFirst(first);
        setLast(last);
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        if (first == null) throw new IllegalArgumentException("not null please");
        if (first.length() < 1) throw new IllegalArgumentException("not empty please");
        if (first.contains(":")) throw new IllegalArgumentException("no ':' please");
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        if (last == null) throw new IllegalArgumentException("not null please");
        if (last.length() < 1) throw new IllegalArgumentException("not empty please");
        if (last.contains(":")) throw new IllegalArgumentException("no ':' please");
        this.last = last;
    }

    @Override
    public String toString() {
        return first + ":" + last;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MyAttribute)) return false;
        MyAttribute that = (MyAttribute) o;
        return Objects.equals(first, that.first) &&
                Objects.equals(last, that.last);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, last);
    }
}
