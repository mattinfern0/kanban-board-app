package io.mattinfern0.kanbanboardapi.organizations;

import io.mattinfern0.kanbanboardapi.organizations.dtos.CreateInviteDto;
import io.mattinfern0.kanbanboardapi.organizations.dtos.InviteDetailDto;
import io.mattinfern0.kanbanboardapi.organizations.dtos.InviteeListItemDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
public class InvitesController {
    final InviteService inviteService;

    @Autowired
    public InvitesController(InviteService inviteService) {
        this.inviteService = inviteService;
    }

    @PostMapping("/invites")
    public InviteDetailDto createInvite(
        Principal principal,
        @Valid @RequestBody CreateInviteDto createInviteDto
    ) {
        return inviteService.createInvite(principal, createInviteDto);
    }

    @GetMapping("/organizations/{organizationId}/invitees")
    public List<InviteeListItemDto> getOrganizationInvitees(
        Principal principal,
        @PathVariable UUID organizationId
    ) {
        return inviteService.getOrganizationInvitees(principal, organizationId);
    }
}
