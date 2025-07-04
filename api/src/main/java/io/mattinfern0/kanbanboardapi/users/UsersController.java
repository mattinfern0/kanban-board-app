package io.mattinfern0.kanbanboardapi.users;

import io.mattinfern0.kanbanboardapi.users.dtos.SignUpDto;
import io.mattinfern0.kanbanboardapi.users.dtos.UserPrivateDetailDto;
import io.mattinfern0.kanbanboardapi.users.dtos.UserSummaryDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {
    final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("")
    public List<UserSummaryDto> getUserList() {
        return usersService.getUserList();
    }

    @PostMapping("/sign-up")
    public UserSummaryDto signUpUser(@RequestBody @Valid SignUpDto signUpDto, Principal principal) {
        String firebaseId = principal.getName();
        return usersService.signUpUser(firebaseId, signUpDto);
    }

    @GetMapping("/me")
    public UserPrivateDetailDto getCurrentUserFullDetails(Principal principal) {
        String firebaseId = principal.getName();
        return usersService.getUserByFirebaseId(firebaseId);
    }
}
