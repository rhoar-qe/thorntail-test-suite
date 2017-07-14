package org.wildfly.swarm.ts.wildfly.msc;

import org.junit.Test;
import org.wildfly.swarm.ts.common.Log;

import java.io.IOException;

import static org.wildfly.swarm.ts.common.LogAssert.assertThat;

public class MscIT {
    @Test
    public void test() throws IOException {
        assertThat(Log.STDOUT).hasLine("HelloServiceActivator running");
        assertThat(Log.STDOUT).hasLine("HelloService started, args: []");
    }
}
