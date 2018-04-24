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
public class MicroProfileJwt10WithRoleMappingTest extends AbstractMicroProfileJwt10Test {
    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackage(MicroProfileJwt10WithRoleMappingTest.class.getPackage())
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsResource(new ClassLoaderAsset("project-defaults-with-role-mapping.yml"), "project-defaults.yml")
                .addAsResource(new ClassLoaderAsset("role-mapping.properties"), "role-mapping.properties");
    }

    @Override
    protected boolean isSuperUser() {
        return true;
    }

    @Override
    protected boolean isMethodWithMissingPermissionsDenied() {
        return false;
    }
}
