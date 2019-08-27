package org.wildfly.swarm.ts.javaee8.bean.validation.services;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.Max;
import java.util.Optional;

@ApplicationScoped
public class OptionalService {
    private Optional<Integer> optionalNumber = Optional.empty();

    public Optional<Integer> getOptionalNumber() {
        return optionalNumber;
    }

    public void setOptionalNumber(Optional<@Max(value = 10, message = "too big") Integer> optionalNumber) {
        this.optionalNumber = optionalNumber;
    }
}
