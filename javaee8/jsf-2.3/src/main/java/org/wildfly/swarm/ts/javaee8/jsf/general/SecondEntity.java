package org.wildfly.swarm.ts.javaee8.jsf.general;

import javax.enterprise.inject.Model;
import javax.faces.annotation.ManagedProperty;
import javax.inject.Inject;
import java.time.LocalDateTime;

@Model
public class SecondEntity {
    @Inject
    @ManagedProperty("#{entity.localDateTime}")
    private LocalDateTime localDateTime;

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }
}
