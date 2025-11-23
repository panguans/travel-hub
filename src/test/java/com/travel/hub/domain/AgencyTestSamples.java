package com.travel.hub.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AgencyTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Agency getAgencySample1() {
        return new Agency().id(1L).description("description1").address("address1").website("website1");
    }

    public static Agency getAgencySample2() {
        return new Agency().id(2L).description("description2").address("address2").website("website2");
    }

    public static Agency getAgencyRandomSampleGenerator() {
        return new Agency()
            .id(longCount.incrementAndGet())
            .description(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString())
            .website(UUID.randomUUID().toString());
    }
}
