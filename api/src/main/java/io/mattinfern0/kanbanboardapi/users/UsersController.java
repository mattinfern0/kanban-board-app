package io.mattinfern0.kanbanboardapi.users;

import io.mattinfern0.kanbanboardapi.users.dtos.SignUpDto;
import io.mattinfern0.kanbanboardapi.users.dtos.UserDto;
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
    public List<UserDto> getUserList() {
        return usersService.getUserList();
    }

    @PostMapping("/sign-up")
    public UserDto signUpUser(@RequestBody @Valid SignUpDto signUpDto) {
        // TODO get firebaseId from auth token
        return usersService.signUpUser(null, signUpDto);
    }

    @GetMapping("/me")
    public UserDto getCurrentUserDetails(Principal principal) {
        // TODO get firebaseId from auth token
        String firebaseId = principal.getName();
        return usersService.getUserByFirebaseId(firebaseId);
    }
}
