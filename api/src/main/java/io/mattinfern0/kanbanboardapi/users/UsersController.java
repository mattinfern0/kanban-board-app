package io.mattinfern0.kanbanboardapi.users;

import io.mattinfern0.kanbanboardapi.users.dtos.UserDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
