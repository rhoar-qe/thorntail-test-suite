package org.wildfly.swarm.ts.javaee8.bean.validation.services;

import org.wildfly.swarm.ts.javaee8.bean.validation.entities.DateEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Past;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class DateService {
    private List<DateEntity> dates = new ArrayList<>();

    public List<DateEntity> getDates() {
        return dates;
    }

    @Valid
    public DateEntity create(
            @Past(message = "must be past") LocalDate past,
            @Future(message = "must be future") LocalDate future,
            @PastOrPresent(message = "must be past or present") LocalDate pastOrPresent,
            @FutureOrPresent(message = "must be future or present") LocalDate futureOrPresent
    ) {
        DateEntity dateEntity = new DateEntity(past, future, pastOrPresent, futureOrPresent);
        dates.add(dateEntity);
        return dateEntity;
    }
}
