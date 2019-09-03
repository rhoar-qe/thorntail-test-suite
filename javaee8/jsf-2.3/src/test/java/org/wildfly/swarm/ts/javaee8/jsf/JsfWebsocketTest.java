package org.wildfly.swarm.ts.javaee8.jsf;

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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.wildfly.swarm.ts.javaee8.jsf.websocket.FakeEndpoint;
import org.wildfly.swarm.ts.javaee8.jsf.websocket.WebSocketBean;
import org.wildfly.swarm.undertow.WARArchive;

import java.io.File;

@RunWith(Arquillian.class)
public class JsfWebsocketTest {
    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(WARArchive.class)
                .addClasses(ApplicationInit.class, WebSocketBean.class, FakeEndpoint.class)
                .addAsWebResource(new File("src/main/webapp/websocket.xhtml"))
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/faces-config.xml"))
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/web.xml"))
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Drone
    private WebDriver driver;

    private WebDriverWait wait;

    @Before
    public void setUp() {
        wait = new WebDriverWait(driver, 3);
        driver.get("http://localhost:8080/websocket.jsf");
    }

    @Test
    @RunAsClient
    public void test() {
        driver.findElement(By.id("form:webSocketMessageText")).sendKeys("first");
        driver.findElement(By.id("form:sendMessage")).click();
        wait.until(ExpectedConditions.textToBePresentInElement(driver.findElement(By.id("messageDiv")), "first,"));

        driver.findElement(By.id("form:webSocketMessageText")).clear();

        driver.findElement(By.id("form:webSocketMessageText")).sendKeys("second");
        driver.findElement(By.id("form:sendMessage")).click();
        wait.until(ExpectedConditions.textToBePresentInElement(driver.findElement(By.id("messageDiv")), "first, second,"));
    }
}
