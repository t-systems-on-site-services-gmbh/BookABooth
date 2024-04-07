package de.tsystems.onsite.bookabooth.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class BoothTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Booth getBoothSample1() {
        return new Booth().id(1L).title("title1");
    }

    public static Booth getBoothSample2() {
        return new Booth().id(2L).title("title2");
    }

    public static Booth getBoothRandomSampleGenerator() {
        return new Booth().id(longCount.incrementAndGet()).title(UUID.randomUUID().toString());
    }
}
