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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.wildfly.swarm.ts.javaee8.jsf.ajax.AjaxBean;
import org.wildfly.swarm.undertow.WARArchive;

import static org.openqa.selenium.support.ui.ExpectedConditions.textToBePresentInElementLocated;

@RunWith(Arquillian.class)
public class JsfAjaxTest {
    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(WARArchive.class)
                .addClasses(ApplicationInit.class, AjaxBean.class)
                .addAsWebResource(new File("src/main/webapp/ajax_method_invocation.xhtml"))
                .addAsWebResource(new File("src/main/webapp/ajax_javascript_executed_from_server.xhtml"))
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/faces-config.xml"))
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/web.xml"))
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Drone
    private WebDriver driver;

    private WebDriverWait wait;

    @Before
    public void init() {
        wait = new WebDriverWait(driver, 5);
    }

    @Test
    @RunAsClient
    public void testMethodInvocation() {
        driver.get("http://localhost:8080/ajax_method_invocation.jsf");
        wait.until(textToBePresentInElementLocated(By.id("output"),"Ajax method invocation successful"));
    }

    @Test
    @RunAsClient
    public void testExecutingJavascriptFromServerAfterAjaxResponse() {
        driver.get("http://localhost:8080/ajax_javascript_executed_from_server.jsf");
        driver.findElement(By.id("form:eval")).click();
        wait.until(textToBePresentInElementLocated(By.id("output"),"JavaScript from server"));
    }
}
