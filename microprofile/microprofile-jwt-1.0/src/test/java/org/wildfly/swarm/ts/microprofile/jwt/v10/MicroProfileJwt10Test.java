package org.wildfly.swarm.ts.microprofile.jwt.v10;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class MicroProfileJwt10Test extends AbstractMicroProfileJwt10Test {
    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackage(MicroProfileJwt10Test.class.getPackage())
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsResource(new ClassLoaderAsset("project-defaults.yml"), "project-defaults.yml")
                .addAsResource(new ClassLoaderAsset("public-key.pem"), "public-key.pem");
    }

    @Override
    protected boolean isSuperUser() {
        return false;
    }

    @Override
    protected boolean isMethodWithMissingPermissionsDenied() {
        return true;
    }

    @Override
    protected boolean isLongerExpirationGracePeriod() {
        return true;
    }
}
