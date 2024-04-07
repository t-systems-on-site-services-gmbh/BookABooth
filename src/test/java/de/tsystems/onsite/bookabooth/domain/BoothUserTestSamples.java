package de.tsystems.onsite.bookabooth.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class BoothUserTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static BoothUser getBoothUserSample1() {
        return new BoothUser().id(1L).phone("phone1").verificationCode(UUID.fromString("23d8dc04-a48b-45d9-a01d-4b728f0ad4aa"));
    }

    public static BoothUser getBoothUserSample2() {
        return new BoothUser().id(2L).phone("phone2").verificationCode(UUID.fromString("ad79f240-3727-46c3-b89f-2cf6ebd74367"));
    }

    public static BoothUser getBoothUserRandomSampleGenerator() {
        return new BoothUser().id(longCount.incrementAndGet()).phone(UUID.randomUUID().toString()).verificationCode(UUID.randomUUID());
    }
}
