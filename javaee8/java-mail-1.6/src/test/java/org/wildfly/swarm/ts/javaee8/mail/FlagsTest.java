package org.wildfly.swarm.ts.javaee8.mail;

import java.util.Arrays;

import javax.mail.Flags;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

public class FlagsTest {

    @Test
    public void flagsConvenienceMethods() {
        Flags flags = new Flags();
        Flags toRetain = new Flags();
        Flags.Flag[] systemFlags = {Flags.Flag.ANSWERED, Flags.Flag.DELETED, Flags.Flag.DRAFT, Flags.Flag.FLAGGED,
                Flags.Flag.RECENT, Flags.Flag.SEEN, Flags.Flag.USER};
        Flags.Flag[] systemFlagsToRetain = {Flags.Flag.ANSWERED, Flags.Flag.SEEN, Flags.Flag.USER};
        String[] userFlags = {"userFlag-1", "userFlag-2"};
        Arrays.stream(systemFlags).forEach(c -> flags.add(c));
        Arrays.stream(userFlags).forEach(c -> flags.add(c));
        Arrays.stream(systemFlagsToRetain).forEach(c -> toRetain.add(c));

        assertThat(flags.getSystemFlags()).hasSize(7);
        assertThat(flags.getUserFlags()).hasSize(2);
        assertTrue(flags.retainAll(toRetain));
        assertThat(flags.getSystemFlags()).contains(Flags.Flag.ANSWERED, Flags.Flag.SEEN, Flags.Flag.USER);
        assertThat(flags.getUserFlags()).hasSize(2);
        toRetain.remove(Flags.Flag.USER);
        assertTrue(flags.retainAll(toRetain));
        assertThat(flags.getSystemFlags()).contains(Flags.Flag.ANSWERED, Flags.Flag.SEEN);
        assertThat(flags.getUserFlags()).hasSize(0);
        flags.clearSystemFlags();
        assertThat(flags.getSystemFlags()).isEmpty();
        flags.add("new_user_flag");
        assertThat(flags.getUserFlags()).isNotEmpty();
        flags.clearUserFlags();
        assertThat(flags.getUserFlags()).isEmpty();
    }

}
