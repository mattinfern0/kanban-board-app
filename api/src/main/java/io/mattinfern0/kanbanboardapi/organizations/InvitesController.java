package io.mattinfern0.kanbanboardapi.organizations;

import io.mattinfern0.kanbanboardapi.organizations.dtos.AcceptInviteDto;
import io.mattinfern0.kanbanboardapi.organizations.dtos.CreateInviteDto;
import io.mattinfern0.kanbanboardapi.organizations.dtos.InviteDetailDto;
import io.mattinfern0.kanbanboardapi.organizations.dtos.InviteeListItemDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    public InviteDetailDto createInvite(
        Principal principal,
        @Valid @RequestBody CreateInviteDto createInviteDto
    ) {
        return inviteService.createInvite(principal, createInviteDto);
    }

    @PostMapping("/invites/accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void acceptInvite(
        Principal principal,
        @Valid @RequestBody AcceptInviteDto acceptInviteDto
    ) {
        inviteService.acceptInvite(principal, acceptInviteDto.token());
    }

    @GetMapping("/users/me/invites")
    public List<InviteDetailDto> getCurrentUserInvites(Principal principal) {
        return inviteService.getCurrentPrincipalInvites(principal);
    }

    @GetMapping("/organizations/{organizationId}/invitees")
    public List<InviteeListItemDto> getOrganizationInvitees(
        Principal principal,
        @PathVariable UUID organizationId
    ) {
        return inviteService.getOrganizationInvitees(principal, organizationId);
    }
}
