package io.mattinfern0.kanbanboardapi.users;

import io.mattinfern0.kanbanboardapi.core.entities.*;
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
        return user.filter(value -> canAccessOrganization(value, organizationId)).isPresent();
    }

    public boolean canAccessOrganization(User user, UUID organizationId) {
        OrganizationMembershipPk membershipPk = new OrganizationMembershipPk(organizationId, user.getId());
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

    public Optional<OrganizationMembership> getMembership(Principal principal, UUID organizationId) {
        Optional<User> user = userRepository.findByFirebaseId(principal.getName());
        if (user.isEmpty()) {
            return Optional.empty();
        }
        OrganizationMembershipPk pk = new OrganizationMembershipPk(organizationId, user.get().getId());
        return organizationMembershipRepository.findById(pk);
    }
}
