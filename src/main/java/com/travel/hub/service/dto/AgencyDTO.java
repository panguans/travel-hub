package com.travel.hub.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.travel.hub.domain.Agency} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AgencyDTO implements Serializable {

    private Long id;

    private String description;

    private String address;

    private String website;

    private Double ratingAvg;

    private UserDTO user_agency;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Double getRatingAvg() {
        return ratingAvg;
    }

    public void setRatingAvg(Double ratingAvg) {
        this.ratingAvg = ratingAvg;
    }

    public UserDTO getUser_agency() {
        return user_agency;
    }

    public void setUser_agency(UserDTO user_agency) {
        this.user_agency = user_agency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AgencyDTO)) {
            return false;
        }

        AgencyDTO agencyDTO = (AgencyDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, agencyDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AgencyDTO{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", address='" + getAddress() + "'" +
            ", website='" + getWebsite() + "'" +
            ", ratingAvg=" + getRatingAvg() +
            ", user_agency=" + getUser_agency() +
            "}";
    }
}
