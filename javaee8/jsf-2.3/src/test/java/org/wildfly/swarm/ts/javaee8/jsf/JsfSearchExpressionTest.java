package org.wildfly.swarm.ts.javaee8.jsf;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.wildfly.swarm.ts.javaee8.jsf.searchexpression.SearchBean;
import org.wildfly.swarm.undertow.WARArchive;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class JsfSearchExpressionTest {
    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(WARArchive.class)
                .addClasses(ApplicationInit.class, SearchBean.class)
                .addAsWebResource(new File("src/main/webapp/search.xhtml"))
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/faces-config.xml"))
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/web.xml"));
    }

    @Drone
    private WebDriver driver;

    @Test
    @RunAsClient
    public void test() {
        driver.get("http://localhost:8080/search.jsf");
        assertThat(driver.getPageSource()).contains("success");
    }
}
