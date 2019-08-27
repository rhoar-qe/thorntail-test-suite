package org.wildfly.swarm.ts.javaee8.bean.validation.entities;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Past;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

public class DateEntity {
    @Past(message = "must be past")
    private LocalDate pastDate;

    @Future(message = "must be future")
    private LocalDate futureDate;

    @PastOrPresent(message = "must be past or present")
    private LocalDate pastOrPresentDate;

    @FutureOrPresent(message = "must be future or present")
    private LocalDate futureOrPresentDate;

    public DateEntity(LocalDate pastDate,
                      LocalDate futureDate,
                      LocalDate pastOrPresentDate,
                      LocalDate futureOrPresentDate) {
        this.pastDate = pastDate;
        this.futureDate = futureDate;
        this.pastOrPresentDate = pastOrPresentDate;
        this.futureOrPresentDate = futureOrPresentDate;
    }

    public LocalDate getPastDate() {
        return pastDate;
    }

    public LocalDate getFutureDate() {
        return futureDate;
    }

    public LocalDate getPastOrPresentDate() {
        return pastOrPresentDate;
    }

    public LocalDate getFutureOrPresentDate() {
        return futureOrPresentDate;
    }
}
