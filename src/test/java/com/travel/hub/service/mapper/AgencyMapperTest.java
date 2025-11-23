package com.travel.hub.service.mapper;

import static com.travel.hub.domain.AgencyAsserts.*;
import static com.travel.hub.domain.AgencyTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AgencyMapperTest {

    private AgencyMapper agencyMapper;

    @BeforeEach
    void setUp() {
        agencyMapper = new AgencyMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAgencySample1();
        var actual = agencyMapper.toEntity(agencyMapper.toDto(expected));
        assertAgencyAllPropertiesEquals(expected, actual);
    }
}
