package io.mattinfern0.kanbanboardapi.boards;

import io.mattinfern0.kanbanboardapi.boards.dtos.BoardColumnDto;
import io.mattinfern0.kanbanboardapi.boards.dtos.BoardDetailDto;
import io.mattinfern0.kanbanboardapi.boards.dtos.CreateBoardDto;
import io.mattinfern0.kanbanboardapi.boards.dtos.UpdateBoardHeaderDTO;
import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import io.mattinfern0.kanbanboardapi.core.exceptions.ResourceNotFoundException;
import io.mattinfern0.kanbanboardapi.core.mappers.BoardDetailDtoMapper;
import io.mattinfern0.kanbanboardapi.core.mappers.BoardSummaryDtoMapper;
import io.mattinfern0.kanbanboardapi.core.repositories.BoardColumnRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.BoardRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationRepository;
import io.mattinfern0.kanbanboardapi.tasks.TaskStatusService;
import io.mattinfern0.kanbanboardapi.users.UserAccessService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("UnitTest")
@ExtendWith(MockitoExtension.class)
class BoardsServiceUnitTest {

    @InjectMocks
    BoardsService boardsService;

    @Mock
    TaskStatusService taskStatusService;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private BoardColumnRepository boardColumnRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private UserAccessService userAccessService;

    @Spy
    private BoardSummaryDtoMapper boardSummaryDtoMapper = Mappers.getMapper(BoardSummaryDtoMapper.class);

    @Spy
    private BoardDetailDtoMapper boardDetailDtoMapper = Mappers.getMapper(BoardDetailDtoMapper.class);

    @Nested
    class CreateBoardTests {
        @Test
        void createNewBoard_throwsResourceNotFoundException_ifOrganizationWithOrganizationIdNotExist() {
            UUID badId = UUID.randomUUID();
            Principal somePrincipal = Mockito.mock(Principal.class);
            Mockito
                .when(userAccessService.canAccessOrganization(Mockito.eq(somePrincipal), Mockito.any()))
                .thenReturn(true);

            CreateBoardDto createBoardDto = new CreateBoardDto(
                "",
                badId
            );

            Mockito.when(organizationRepository.findById(badId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> {
                boardsService.createNewBoard(somePrincipal, createBoardDto);
            });
        }

        @Test
        void createNewBoard_newBoardHasCorrectOrganization() {
            Organization testOrganization = createMockOrganization();

            Principal somePrincipal = Mockito.mock(Principal.class);
            Mockito
                .when(userAccessService.canAccessOrganization(Mockito.eq(somePrincipal), Mockito.any()))
                .thenReturn(true);


            CreateBoardDto createBoardDto = new CreateBoardDto(
                "",
                testOrganization.getId()
            );

            Mockito
                .when(organizationRepository.findById(testOrganization.getId()))
                .thenReturn(Optional.of(testOrganization));

            BoardDetailDto result = boardsService.createNewBoard(somePrincipal, createBoardDto);
            assert result.organizationId().equals(createBoardDto.organizationId());
        }

        @Test
        void createNewBoard_newBoardHasCorrectTitle() {
            Organization testOrganization = createMockOrganization();
            Principal somePrincipal = Mockito.mock(Principal.class);
            Mockito
                .when(userAccessService.canAccessOrganization(Mockito.eq(somePrincipal), Mockito.any()))
                .thenReturn(true);

            CreateBoardDto createBoardDto = new CreateBoardDto(
                "My Super Evil Inator Plans",
                testOrganization.getId()
            );

            Mockito
                .when(organizationRepository.findById(testOrganization.getId()))
                .thenReturn(Optional.of(testOrganization));

            BoardDetailDto result = boardsService.createNewBoard(somePrincipal, createBoardDto);
            assert result.title().equals(createBoardDto.title());
        }

        @Test
        void createNewBoard_hasExpectedDefaultColumns() {
            Organization testOrganization = createMockOrganization();

            Principal somePrincipal = Mockito.mock(Principal.class);
            Mockito
                .when(userAccessService.canAccessOrganization(Mockito.eq(somePrincipal), Mockito.any()))
                .thenReturn(true);

            CreateBoardDto createBoardDto = new CreateBoardDto(
                "My Super Evil Inator Plans",
                testOrganization.getId()
            );

            Mockito
                .when(organizationRepository.findById(testOrganization.getId()))
                .thenReturn(Optional.of(testOrganization));

            List<String> expectedColumnTitles = List.of("Back Log", "Todo", "In-Progress", "Done");

            BoardDetailDto result = boardsService.createNewBoard(somePrincipal, createBoardDto);

            for (int i = 0; i < expectedColumnTitles.size(); i++) {
                String expectedTitle = expectedColumnTitles.get(i);
                BoardColumnDto columnDto = result.boardColumns().get(i);

                assert columnDto.title().equals(expectedTitle);

            }
        }

        @Test
        void createNewBoard_hasNoTasks() {
            Organization testOrganization = createMockOrganization();
            Principal somePrincipal = Mockito.mock(Principal.class);
            Mockito
                .when(userAccessService.canAccessOrganization(Mockito.eq(somePrincipal), Mockito.any()))
                .thenReturn(true);

            CreateBoardDto createBoardDto = new CreateBoardDto(
                "My Super Evil Inator Plans",
                testOrganization.getId()
            );

            BoardDetailDto result = boardsService.createNewBoard(somePrincipal, createBoardDto);

            for (BoardColumnDto columnDto : result.boardColumns()) {
                assert columnDto.tasks().isEmpty();
            }
        }

        @Test
        void shouldThrow_AccessDenied_ifUserIsNotPartOfOrganization() {
            UUID testOrganizationId = UUID.randomUUID();
            Organization testOrganization = new Organization();
            testOrganization.setId(testOrganizationId);
            Principal somePrincipal = Mockito.mock(Principal.class);

            CreateBoardDto createBoardDto = new CreateBoardDto(
                "My Super Evil Inator Plans",
                testOrganization.getId()
            );

            assertThrows(AccessDeniedException.class, () -> {
                boardsService.createNewBoard(somePrincipal, createBoardDto);
            });
        }

        Organization createMockOrganization() {
            UUID testOrganizationId = UUID.randomUUID();
            Organization testOrganization = new Organization();
            testOrganization.setId(testOrganizationId);
            Mockito
                .when(organizationRepository.findById(testOrganizationId))
                .thenReturn(Optional.of(testOrganization));
            return testOrganization;
        }
    }

    @Nested
    class UpdateBoardTests {
        @Test
        void shouldThrow_AccessDenied_ifPrincipalCannotAccessBoard() {
            Principal somePrincipal = Mockito.mock(Principal.class);
            UpdateBoardHeaderDTO testUpdateBoardDto = new UpdateBoardHeaderDTO(
                "Test title"
            );
            UUID testBoardId = UUID.randomUUID();

            assertThrows(AccessDeniedException.class, () -> {
                boardsService.updateBoard(somePrincipal, testBoardId, testUpdateBoardDto);
            });
        }

        @Test
        void shouldThrow_ResourceNotFoundException_ifBoardDoesNotExist() {
            Principal somePrincipal = Mockito.mock(Principal.class);
            Mockito
                .when(userAccessService.canAccessBoard(Mockito.eq(somePrincipal), Mockito.any()))
                .thenReturn(true);

            UUID testBoardId = UUID.randomUUID();
            Mockito.when(boardRepository.findById(testBoardId)).thenReturn(Optional.empty());
            UpdateBoardHeaderDTO testUpdateBoardDto = new UpdateBoardHeaderDTO(
                "Test title"
            );
            assertThrows(ResourceNotFoundException.class, () -> {
                boardsService.updateBoard(somePrincipal, testBoardId, testUpdateBoardDto);
            });

        }
    }
}