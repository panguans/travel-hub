package com.travel.hub.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.travel.hub.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AgencyDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AgencyDTO.class);
        AgencyDTO agencyDTO1 = new AgencyDTO();
        agencyDTO1.setId(1L);
        AgencyDTO agencyDTO2 = new AgencyDTO();
        assertThat(agencyDTO1).isNotEqualTo(agencyDTO2);
        agencyDTO2.setId(agencyDTO1.getId());
        assertThat(agencyDTO1).isEqualTo(agencyDTO2);
        agencyDTO2.setId(2L);
        assertThat(agencyDTO1).isNotEqualTo(agencyDTO2);
        agencyDTO1.setId(null);
        assertThat(agencyDTO1).isNotEqualTo(agencyDTO2);
    }
}
