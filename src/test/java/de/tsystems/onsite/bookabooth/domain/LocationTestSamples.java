package de.tsystems.onsite.bookabooth.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class LocationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Location getLocationSample1() {
        return new Location().id(1L).location("location1");
    }

    public static Location getLocationSample2() {
        return new Location().id(2L).location("location2");
    }

    public static Location getLocationRandomSampleGenerator() {
        return new Location().id(longCount.incrementAndGet()).location(UUID.randomUUID().toString());
    }
}
