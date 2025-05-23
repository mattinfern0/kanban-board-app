package io.mattinfern0.kanbanboardapi.organizations.mappers;

import io.mattinfern0.kanbanboardapi.core.entities.OrganizationInvite;
import io.mattinfern0.kanbanboardapi.organizations.dtos.InviteDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface InviteDtoMapper {
    @Mapping(target = "organization.name", source = "organization.displayName")
    InviteDto entityToDto(OrganizationInvite entity);

    List<InviteDto> entityListToDtoList(List<OrganizationInvite> entities);
}
