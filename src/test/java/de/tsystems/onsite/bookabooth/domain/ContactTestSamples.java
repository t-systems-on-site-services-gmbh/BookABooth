package de.tsystems.onsite.bookabooth.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ContactTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Contact getContactSample1() {
        return new Contact().id(1L).firstName("firstName1").lastName("lastName1").mail("mail1").phone("phone1");
    }

    public static Contact getContactSample2() {
        return new Contact().id(2L).firstName("firstName2").lastName("lastName2").mail("mail2").phone("phone2");
    }

    public static Contact getContactRandomSampleGenerator() {
        return new Contact()
            .id(longCount.incrementAndGet())
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .mail(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString());
    }
}
