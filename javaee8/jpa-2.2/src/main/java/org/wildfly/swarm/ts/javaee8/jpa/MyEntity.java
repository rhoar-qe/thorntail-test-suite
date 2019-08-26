package org.wildfly.swarm.ts.javaee8.jpa;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
@NamedQuery(name = "MyEntity.findAll", query = "SELECT e FROM MyEntity e")
@NamedQuery(name = "MyEntity.findAllIds", query = "SELECT e.id FROM MyEntity e")
public class MyEntity {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private LocalDateTime localDateTime;

    // TODO without the @Convert annotation, Hibernate instantiates the converter directly, instead of using CDI
    //      need to investigate deeper and probably file a Hibernate bug
    @Convert(converter = MyAttributeConverter.class)
    private MyAttribute myAttribute;

    public MyEntity() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public MyAttribute getMyAttribute() {
        return myAttribute;
    }

    public void setMyAttribute(MyAttribute myAttribute) {
        this.myAttribute = myAttribute;
    }

    @Override
    public String toString() {
        return id + ": " + localDateTime.format(formatter) + ", " + myAttribute;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyEntity myEntity = (MyEntity) o;
        return id == myEntity.id &&
                Objects.equals(localDateTime, myEntity.localDateTime) &&
                Objects.equals(myAttribute, myEntity.myAttribute);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, localDateTime, myAttribute);
    }
}
