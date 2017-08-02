package org.wildfly.swarm.ts.wildfly.topology.webapp;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.assertj.core.api.Assertions;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.msc.service.ServiceActivator;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.wildfly.swarm.spi.api.JARArchive;
import org.wildfly.swarm.topology.AdvertisementHandle;
import org.wildfly.swarm.topology.Topology;
import org.wildfly.swarm.undertow.WARArchive;

import javax.naming.NamingException;
import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.support.ui.ExpectedConditions.not;
import static org.openqa.selenium.support.ui.ExpectedConditions.textToBePresentInElementLocated;

@RunWith(Arquillian.class)
public class TopologyWebappTest {
    @Drone
    private WebDriver browser;

    private WebDriverWait wait;

    @Before
    public void setUp() {
        wait = new WebDriverWait(browser, 10);
    }

    @Deployment
    public static Archive deployment() {
        Archive war = ShrinkWrap.create(WebArchive.class)
                .addPackage(HelloTest.class.getPackage())
                .addAsServiceProvider(ServiceActivator.class, MockTopologyConnectorServiceActivator.class)
                .addAsWebResource(new File("src/main/webapp/index.html"), "index.html")
                .addAsWebResource(new File("src/main/webapp/jquery-3.2.1.min.js"), "jquery-3.2.1.min.js");
        war.as(JARArchive.class).addModule("org.wildfly.swarm.topology", "runtime"); // needed for mock topology connector
        war.as(JARArchive.class).addModule("org.jboss.as.network");                  // needed for mock topology connector
        return war;
    }

    @Test
    @RunAsClient
    @InSequence(1)
    public void loadPageAndCheckInitialTopology() throws IOException, InterruptedException {
        browser.get("http://localhost:8080/");
        wait.until(textToBePresentInElementLocated(By.id("current-topology"), "hello-test"));
    }

    @Test
    @RunAsClient
    @InSequence(2)
    public void advertiseAnotherService() throws IOException, NamingException {
        HttpResponse response = Request.Put("http://localhost:8080/test/topology/foobar").execute().returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
    }

    @Test
    @RunAsClient
    @InSequence(3)
    public void checkNewServiceIsListedOnThePage() throws IOException {
        wait.until(textToBePresentInElementLocated(By.id("current-topology"), "foobar"));
    }

    @Test
    @RunAsClient
    @InSequence(4)
    public void unadvertiseTheNewService() throws IOException, NamingException {
        HttpResponse response = Request.Delete("http://localhost:8080/test/topology/foobar").execute().returnResponse();
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
    }

    @Test
    @RunAsClient
    @InSequence(5)
    public void checkRemovedServiceIsNoLongerListedOnThePage() throws IOException {
        wait.until(not(textToBePresentInElementLocated(By.id("current-topology"), "foobar")));
    }

    @Test
    @RunAsClient
    @InSequence(6)
    public void sendGetRequest() throws IOException {
        WebElement input = browser.findElement(By.id("request-input"));
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE, "Ladicek", Keys.TAB);
        browser.findElement(By.id("do-get-request")).click();

        wait.until(textToBePresentInElementLocated(By.id("result"), "Hello, Ladicek!"));
    }

    @Test
    @RunAsClient
    @InSequence(7)
    public void sendPostRequest() throws IOException {
        WebElement input = browser.findElement(By.id("request-input"));
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE, "Albert Einstein", Keys.TAB);
        browser.findElement(By.id("do-post-request")).click();

        wait.until(textToBePresentInElementLocated(By.id("result"), "Hi there, Albert Einstein!"));
    }
}
