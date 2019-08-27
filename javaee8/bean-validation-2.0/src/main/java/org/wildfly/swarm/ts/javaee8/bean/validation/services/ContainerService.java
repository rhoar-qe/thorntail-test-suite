package org.wildfly.swarm.ts.javaee8.bean.validation.services;

import org.wildfly.swarm.ts.javaee8.bean.validation.entities.EmailEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ContainerService {
    private Map<@Positive Integer, @Valid EmailEntity> map = new HashMap<>();

    private Map<Integer, List<@Valid EmailEntity>> nestedMap = new HashMap<>();

    public Map<Integer, EmailEntity> getMap() {
        return map;
    }

    public Map<Integer, List<EmailEntity>> getNestedMap() {
        return nestedMap;
    }

    @Valid
    public EmailEntity add(@Positive(message = "just positive") Integer number,
                           @Valid EmailEntity email
    ) {
        return map.put(number, email);
    }

    @Valid
    public List<EmailEntity> add(Integer number,
                                 @Valid List<EmailEntity> emails
    ) {
        nestedMap.put(number, emails);
        return emails;
    }
}
