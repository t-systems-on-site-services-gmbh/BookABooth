package de.tsystems.onsite.bookabooth.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class SystemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static System getSystemSample1() {
        return new System().id(1L);
    }

    public static System getSystemSample2() {
        return new System().id(2L);
    }

    public static System getSystemRandomSampleGenerator() {
        return new System().id(longCount.incrementAndGet());
    }
}
