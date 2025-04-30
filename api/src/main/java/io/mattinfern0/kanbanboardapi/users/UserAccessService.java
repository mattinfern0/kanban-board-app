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

    public boolean canAccessOrganization(UUID userId, UUID organizationId) {
        OrganizationMembershipPk membershipPk = new OrganizationMembershipPk();
        membershipPk.setOrganizationId(organizationId);
        membershipPk.setUserId(userId);
        return organizationMembershipRepository.existsByPk(membershipPk);
    }

    public boolean canAccessOrganization(String firebaseId, UUID organizationId) {
        Optional<User> user = userRepository.findByFirebaseId(firebaseId);
        if (user.isEmpty()) {
            return false;
        }
        return canAccessOrganization(user.get().getId(), organizationId);
    }

    public boolean canAccessBoard(String firebaseId, UUID boardId) {
        Optional<Board> board = boardRepository.findById(boardId);
        if (board.isEmpty()) {
            return false;
        }

        return canAccessOrganization(firebaseId, board.get().getOrganization().getId());
    }

    public boolean canAccessTask(String firebaseId, UUID taskId) {
        Optional<Task> task = taskRepository.findById(taskId);
        if (task.isEmpty()) {
            return false;
        }

        return canAccessOrganization(firebaseId, task.get().getOrganization().getId());
    }
}
