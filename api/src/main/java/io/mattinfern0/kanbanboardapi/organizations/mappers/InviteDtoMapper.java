package io.mattinfern0.kanbanboardapi.organizations.mappers;

import io.mattinfern0.kanbanboardapi.core.entities.OrganizationInvite;
import io.mattinfern0.kanbanboardapi.organizations.dtos.InviteDetailDto;
import io.mattinfern0.kanbanboardapi.organizations.dtos.InviteeListItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface InviteDtoMapper {
    @Mapping(target = "organization.name", source = "organization.displayName")
    InviteDetailDto entityToDetailDto(OrganizationInvite entity);

    List<InviteDetailDto> entityListToDetailDtoList(List<OrganizationInvite> entities);

    InviteeListItemDto entityToInviteeListItemDto(OrganizationInvite entity);

    List<InviteeListItemDto> entityListToInviteeList(List<OrganizationInvite> entities);
}
