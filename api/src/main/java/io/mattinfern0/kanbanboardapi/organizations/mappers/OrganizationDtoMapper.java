package io.mattinfern0.kanbanboardapi.organizations.mappers;

import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import io.mattinfern0.kanbanboardapi.core.entities.OrganizationMembership;
import io.mattinfern0.kanbanboardapi.organizations.dtos.OrganizationDetailsDto;
import io.mattinfern0.kanbanboardapi.organizations.dtos.OrganizationMemberDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface OrganizationDtoMapper {
    @Mapping(target = "members", source = "memberships")
    OrganizationDetailsDto organizationEntitytoDetailsDto(Organization organization);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "role", source = "role.codename")
    OrganizationMemberDto organizationMembershipEntityToDto(OrganizationMembership organizationMembership);

    List<OrganizationMemberDto> organizationMembershipEntitiesToDtoList(List<OrganizationMembership> members);
}
