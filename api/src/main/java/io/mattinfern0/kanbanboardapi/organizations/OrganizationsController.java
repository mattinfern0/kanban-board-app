package io.mattinfern0.kanbanboardapi.organizations;

import io.mattinfern0.kanbanboardapi.organizations.dtos.OrganizationDetailsDto;
import io.mattinfern0.kanbanboardapi.organizations.dtos.UpdateMembershipDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/organizations")
public class OrganizationsController {
    final OrganizationService organizationService;

    @Autowired
    public OrganizationsController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping("/{organizationId}")
    public OrganizationDetailsDto getOrganizationDetail(Principal principal, @PathVariable UUID organizationId) {
        return organizationService.getOrganizationDetails(principal, organizationId);
    }

    @PutMapping("/{organizationId}/members/{userId}")
    public void updateMembership(Principal principal, @PathVariable UUID organizationId, @PathVariable UUID userId, @RequestBody UpdateMembershipDto updateMembershipDto) {
        organizationService.updateMembership(principal, organizationId, userId, updateMembershipDto);
    }

    @DeleteMapping("/{organizationId}/members/{userId}")
    public void deleteMember(Principal principal, @PathVariable UUID organizationId, @PathVariable UUID userId) {
        organizationService.deleteMembership(principal, organizationId, userId);
    }
}
