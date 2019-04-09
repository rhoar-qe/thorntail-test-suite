package org.wildfly.swarm.ts.microprofile.jwt.v11;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ConfigPublicKeyJwt11Test extends AbstractConfigJwt11Test {
    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackage(ConfigPublicKeyJwt11Test.class.getPackage())
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsManifestResource(new ClassLoaderAsset("config-publickey.properties"),
                                       "microprofile-config.properties")
                .addAsResource(new ClassLoaderAsset("project-defaults-security.yml"), "project-defaults.yml");
    }
}
