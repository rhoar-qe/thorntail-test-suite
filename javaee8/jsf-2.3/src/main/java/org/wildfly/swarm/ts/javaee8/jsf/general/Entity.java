package org.wildfly.swarm.ts.javaee8.jsf.general;

import javax.enterprise.inject.Model;
import java.time.LocalDateTime;

@Model
public class Entity {
    private LocalDateTime localDateTime = LocalDateTime.of(2012, 12, 21, 0, 0);

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }
}
