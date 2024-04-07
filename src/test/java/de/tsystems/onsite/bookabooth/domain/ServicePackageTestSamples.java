package de.tsystems.onsite.bookabooth.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ServicePackageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ServicePackage getServicePackageSample1() {
        return new ServicePackage().id(1L).name("name1").description("description1");
    }

    public static ServicePackage getServicePackageSample2() {
        return new ServicePackage().id(2L).name("name2").description("description2");
    }

    public static ServicePackage getServicePackageRandomSampleGenerator() {
        return new ServicePackage()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
