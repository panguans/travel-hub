package com.travel.hub.service.mapper;

import com.travel.hub.domain.Agency;
import com.travel.hub.domain.User;
import com.travel.hub.service.dto.AgencyDTO;
import com.travel.hub.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Agency} and its DTO {@link AgencyDTO}.
 */
@Mapper(componentModel = "spring")
public interface AgencyMapper extends EntityMapper<AgencyDTO, Agency> {
    @Mapping(target = "user_agency", source = "user_agency", qualifiedByName = "userLogin")
    AgencyDTO toDto(Agency s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
