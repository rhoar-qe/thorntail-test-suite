package org.wildfly.swarm.ts.javaee8.mail;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeMessage;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HeadersTest {
    private Session mailSession;

    @Test
    public void updateHeadersSetsDateHeader() throws MessagingException {
        MimeMessage message = new MimeMessage(mailSession);
        assertThat(message.getHeader("Date")).isNull();
        message.saveChanges();
        assertThat(message.getHeader("Date")).isNotNull().hasSize(1);
    }

    @Test
    public void updateHeadersDoesNotChangeDateWhenAlreadySet() throws MessagingException {
        MimeMessage message = new MimeMessage(mailSession);
        message.setHeader("Date", "Mon, 6 Jan 2020 09:57:37 +0100 (CET)");
        assertThat(message.getHeader("Date")).isNotNull().contains("Mon, 6 Jan 2020 09:57:37 +0100 (CET)");
        message.saveChanges();
        assertThat(message.getHeader("Date")).isNotNull().contains("Mon, 6 Jan 2020 09:57:37 +0100 (CET)");
    }

    @Test
    public void internetHeadersConstructorWithInputStream() throws MessagingException, IOException {
        String string = "header1:value1\nheader2:value2\n\nbody";
        BufferedInputStream stream = new BufferedInputStream(new ByteArrayInputStream(string.getBytes()));
        InternetHeaders headers = new InternetHeaders(stream, true);
        assertThat(headers.getHeader("header1")).contains("value1");
        assertThat(headers.getHeader("header2")).contains("value2");

        String body = new BufferedReader(new InputStreamReader(stream)).readLine();
        assertThat(body).isEqualTo("body");
    }

    @Test
    public void autoCloseable() {
        assertThat(AutoCloseable.class).isAssignableFrom(Store.class);
        assertThat(AutoCloseable.class).isAssignableFrom(Transport.class);
        assertThat(AutoCloseable.class).isAssignableFrom(Folder.class);
    }

    @Test
    public void addressToUnicodeString() throws AddressException {
        String str = InternetAddress.toUnicodeString(new InternetAddress[]{
                new InternetAddress("address-1", false),
                new InternetAddress("address-2", false)
        });
        assertThat(str).isEqualTo("address-1, address-2");
    }
}