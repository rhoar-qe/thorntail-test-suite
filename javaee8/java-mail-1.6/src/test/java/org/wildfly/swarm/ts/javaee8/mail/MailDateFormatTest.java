package org.wildfly.swarm.ts.javaee8.mail;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;

import javax.mail.internet.MailDateFormat;

import org.junit.Assert;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MailDateFormatTest {

    @Test
    public void correctMailDateFormat() {
        try {
            new MailDateFormat().parse("Mon, 6 Jan 2020 09:57:37 +0100");
        } catch (ParseException ex) {
            Assert.fail("Parse exception occurred on a valid date format");
        }
    }

    @Test
    public void incorrectMailDateFormatThrowsParseException() {
        assertThatThrownBy(() -> new MailDateFormat().parse("abcdefgh")).isExactlyInstanceOf(ParseException.class);
    }

    @Test
    public void unsupportedMailDateFormatMethods() {
        assertThatThrownBy(() -> new MailDateFormat().applyLocalizedPattern("test")).isExactlyInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> new MailDateFormat().applyPattern("test")).isExactlyInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> new MailDateFormat().get2DigitYearStart()).isExactlyInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> new MailDateFormat().set2DigitYearStart(Date.from(Instant.now())))
                .isExactlyInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> new MailDateFormat().setDateFormatSymbols(DateFormatSymbols.getInstance()))
                .isExactlyInstanceOf(UnsupportedOperationException.class);
    }
}
