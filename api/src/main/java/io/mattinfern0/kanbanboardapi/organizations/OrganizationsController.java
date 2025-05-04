package io.mattinfern0.kanbanboardapi.organizations;

import io.mattinfern0.kanbanboardapi.organizations.dtos.OrganizationDetailsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
