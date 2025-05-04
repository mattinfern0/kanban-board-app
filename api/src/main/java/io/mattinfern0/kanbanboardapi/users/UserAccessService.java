package io.mattinfern0.kanbanboardapi.users;

import io.mattinfern0.kanbanboardapi.core.entities.Board;
import io.mattinfern0.kanbanboardapi.core.entities.OrganizationMembershipPk;
import io.mattinfern0.kanbanboardapi.core.entities.Task;
import io.mattinfern0.kanbanboardapi.core.entities.User;
import io.mattinfern0.kanbanboardapi.core.repositories.BoardRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationMembershipRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.TaskRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserAccessService {
    final OrganizationMembershipRepository organizationMembershipRepository;
    final UserRepository userRepository;
    final BoardRepository boardRepository;
    final TaskRepository taskRepository;

    public UserAccessService(OrganizationMembershipRepository organizationMembershipRepository, UserRepository userRepository, BoardRepository boardRepository, TaskRepository taskRepository) {
        this.organizationMembershipRepository = organizationMembershipRepository;
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
        this.taskRepository = taskRepository;
    }

    public boolean canAccessOrganization(Principal principal, UUID organizationId) {
        Optional<User> user = userRepository.findByFirebaseId(principal.getName());
        if (user.isEmpty()) {
            return false;
        }

        OrganizationMembershipPk membershipPk = new OrganizationMembershipPk();
        membershipPk.setOrganizationId(organizationId);
        membershipPk.setUserId(user.get().getId());
        return organizationMembershipRepository.existsByPk(membershipPk);
    }

    public boolean canAccessBoard(Principal principal, UUID boardId) {
        Optional<Board> board = boardRepository.findById(boardId);
        return board
            .filter(value -> canAccessOrganization(principal, value.getOrganization().getId()))
            .isPresent();
    }

    public boolean canAccessTask(Principal principal, UUID taskId) {
        Optional<Task> task = taskRepository.findById(taskId);
        return task
            .filter(value -> canAccessOrganization(principal, value.getOrganization().getId()))
            .isPresent();
    }
}
