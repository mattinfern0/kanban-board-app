package io.mattinfern0.kanbanboardapi.organizations;

import io.mattinfern0.kanbanboardapi.organizations.dtos.CreateInviteDto;
import io.mattinfern0.kanbanboardapi.organizations.dtos.InviteDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invites")
public class InvitesController {
    final InviteService inviteService;

    @Autowired
    public InvitesController(InviteService inviteService) {
        this.inviteService = inviteService;
    }

    @PostMapping()
    public InviteDto createInvite(@Valid @RequestBody CreateInviteDto createInviteDto) {
        return inviteService.createInvite(createInviteDto);
    }

    @GetMapping()
    public List<InviteDto> getInvites() {
        return List.of();
    }
}
