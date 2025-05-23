package io.mattinfern0.kanbanboardapi.organizations;

import io.mattinfern0.kanbanboardapi.organizations.dtos.OrganizationDetailsDto;
import io.mattinfern0.kanbanboardapi.organizations.dtos.OrganizationMemberDto;
import io.mattinfern0.kanbanboardapi.organizations.dtos.UpdateMembershipDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public OrganizationMemberDto updateMembership(Principal principal, @PathVariable UUID organizationId, @PathVariable UUID userId, @Valid @RequestBody UpdateMembershipDto updateMembershipDto) {
        return organizationService.updateMembership(principal, organizationId, userId, updateMembershipDto);
    }

    @DeleteMapping("/{organizationId}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMember(Principal principal, @PathVariable UUID organizationId, @PathVariable UUID userId) {
        organizationService.deleteMembership(principal, organizationId, userId);
    }
}
