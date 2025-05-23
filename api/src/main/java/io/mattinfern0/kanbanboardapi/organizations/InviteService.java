package io.mattinfern0.kanbanboardapi.organizations;

import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import io.mattinfern0.kanbanboardapi.core.entities.OrganizationInvite;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationInviteRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationRepository;
import io.mattinfern0.kanbanboardapi.organizations.dtos.CreateInviteDto;
import io.mattinfern0.kanbanboardapi.organizations.dtos.InviteDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class InviteService {
    final OrganizationRepository organizationRepository;
    final OrganizationInviteRepository organizationInviteRepository;

    @Autowired
    public InviteService(OrganizationRepository organizationRepository, OrganizationInviteRepository organizationInviteRepository) {
        this.organizationRepository = organizationRepository;
        this.organizationInviteRepository = organizationInviteRepository;
    }

    public InviteDto createInvite(CreateInviteDto createInviteDto) {
        Organization organization = organizationRepository
            .findById(createInviteDto.organizationId())
            .orElseThrow(() -> new IllegalArgumentException("Organization with id not found"));

        OrganizationInvite inviteEntity = new OrganizationInvite();
        inviteEntity.setId(UUID.randomUUID());
        inviteEntity.setOrganization(organization);

        organizationInviteRepository.save(inviteEntity);

        return null;
    }

    public void acceptInvite(String inviteToken) {

    }
}
