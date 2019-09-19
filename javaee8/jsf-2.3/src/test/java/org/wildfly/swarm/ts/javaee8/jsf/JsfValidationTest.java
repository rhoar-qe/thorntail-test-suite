package org.wildfly.swarm.ts.javaee8.jsf;

import java.io.File;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.wildfly.swarm.ts.javaee8.jsf.validation.PasswordValidator;
import org.wildfly.swarm.undertow.WARArchive;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class JsfValidationTest {
    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(WARArchive.class)
                .addPackage(PasswordValidator.class.getPackage())
                .addClass(ApplicationInit.class)
                .addAsWebResource(new File("src/main/webapp/validate.xhtml"))
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/faces-config.xml"))
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/web.xml"))
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Drone
    private WebDriver driver;

    private WebElement password1Input;

    private WebElement password2Input;

    private WebElement passwordSubmit;

    @Before
    public void setUp() {
        driver.get("http://localhost:8080/validate.jsf");
        password1Input = driver.findElement(By.id("form:password1"));
        password2Input = driver.findElement(By.id("form:password2"));
        passwordSubmit = driver.findElement(By.id("form:submitButton"));
    }

    @Test
    @RunAsClient
    public void fieldTooShort() {
        password1Input.sendKeys("passw");
        password2Input.sendKeys("password");
        passwordSubmit.submit();
        assertThat(driver.getPageSource()).contains("password has wrong size");
    }

    @Test
    @RunAsClient
    public void fieldTooLong() {
        password1Input.sendKeys("password");
        password2Input.sendKeys("passwordisreallytoolong");
        passwordSubmit.submit();
        assertThat(driver.getPageSource()).contains("password has wrong size");
    }

    @Test
    @RunAsClient
    public void fieldsDoNotMatch() {
        password1Input.sendKeys("password1");
        password2Input.sendKeys("password2");
        passwordSubmit.submit();
        assertThat(driver.getPageSource()).contains("password fields must match");
    }

    @Test
    @RunAsClient
    public void fieldsRightSizeAndMatch() {
        password1Input.sendKeys("password");
        password2Input.sendKeys("password");
        passwordSubmit.submit();
        assertThat(driver.getPageSource()).doesNotContain("password has wrong size");
        assertThat(driver.getPageSource()).doesNotContain("password fields must match");
    }
}
