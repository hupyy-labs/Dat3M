package com.dat3m.dartagnan.llvm;

import com.dat3m.dartagnan.configuration.Arch;
import com.dat3m.dartagnan.configuration.Method;
import com.dat3m.dartagnan.configuration.OptionNames;
import com.dat3m.dartagnan.configuration.Property;
import com.dat3m.dartagnan.utils.Result;
import com.dat3m.dartagnan.utils.rules.Provider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.sosy_lab.common.configuration.ConfigurationBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;

import static com.dat3m.dartagnan.configuration.Arch.IMM;
import static com.dat3m.dartagnan.utils.Result.*;

// Termination with program.processing.removeWriteOnlyMemory=true. LivenessTest holds the same programs without it.
@RunWith(Parameterized.class)
public class WriteOnlyMemoryLivenessTest extends AbstractCTest {

    public WriteOnlyMemoryLivenessTest(String name, Arch target, Result expected) {
        super(name, target, expected);
    }

    @Override
    protected long getTimeout() {
        return 60000;
    }

    @Override
    protected Provider<Integer> getBoundProvider() {
        return () -> 2;
    }

    @Override
    protected Provider<EnumSet<Property>> getPropertyProvider() {
        return () -> EnumSet.of(Property.TERMINATION);
    }

    @Override
    protected ConfigurationBuilder additionalConfig(ConfigurationBuilder builder) {
        return super.additionalConfig(builder).setOption(OptionNames.REMOVE_WRITE_ONLY_MEMORY, "true");
    }

    @Parameterized.Parameters(name = "{index}: {0}, target={1}")
    public static Iterable<Object[]> data() throws IOException {
        return Arrays.asList(new Object[][]{
                {"nontermination/write_only_counter_starvation", IMM, FAIL},
                {"nontermination/write_only_counter_terminates", IMM, PASS},
                // The counter is read, so it is kept, and the loop is never fully unrolled.
                {"nontermination/read_counter_controls_loop", IMM, UNKNOWN},
        });
    }

    @Test
    public void testRefinement() throws Exception {
        testSolver(Method.LAZY);
    }
}
