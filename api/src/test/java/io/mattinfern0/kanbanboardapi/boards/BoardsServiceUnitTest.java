package io.mattinfern0.kanbanboardapi.boards;

import io.mattinfern0.kanbanboardapi.boards.dtos.BoardColumnDto;
import io.mattinfern0.kanbanboardapi.boards.dtos.BoardDetailDto;
import io.mattinfern0.kanbanboardapi.boards.dtos.CreateBoardDto;
import io.mattinfern0.kanbanboardapi.core.entities.Organization;
import io.mattinfern0.kanbanboardapi.core.exceptions.ResourceNotFoundException;
import io.mattinfern0.kanbanboardapi.core.mappers.BoardDetailDtoMapper;
import io.mattinfern0.kanbanboardapi.core.mappers.BoardSummaryDtoMapper;
import io.mattinfern0.kanbanboardapi.core.repositories.BoardColumnRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.BoardRepository;
import io.mattinfern0.kanbanboardapi.core.repositories.OrganizationRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private BoardRepository boardRepository;

    @Mock
    private BoardColumnRepository boardColumnRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Spy
    private BoardSummaryDtoMapper boardSummaryDtoMapper = Mappers.getMapper(BoardSummaryDtoMapper.class);

    @Spy
    private BoardDetailDtoMapper boardDetailDtoMapper = Mappers.getMapper(BoardDetailDtoMapper.class);


    @Test
    void createNewBoard_throwsResourceNotFoundException_ifOrganizationWithOrganizationIdNotExist() {
        UUID badId = UUID.randomUUID();

        CreateBoardDto createBoardDto = new CreateBoardDto();
        createBoardDto.setTitle("");
        createBoardDto.setOrganizationId(badId);

        Mockito.when(organizationRepository.findById(badId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            boardsService.createNewBoard(createBoardDto);
        });
    }

    @Test
    void createNewBoard_newBoardHasCorrectOrganization() {
        UUID testOrganizationId = UUID.randomUUID();
        Organization testOrganization = new Organization();
        testOrganization.setId(testOrganizationId);

        CreateBoardDto createBoardDto = new CreateBoardDto();
        createBoardDto.setTitle("");
        createBoardDto.setOrganizationId(testOrganizationId);

        Mockito.when(organizationRepository.findById(testOrganizationId)).thenReturn(Optional.of(testOrganization));

        BoardDetailDto result = boardsService.createNewBoard(createBoardDto);
        assert result.getOrganizationId().equals(createBoardDto.getOrganizationId());
    }

    @Test
    void createNewBoard_newBoardHasCorrectTitle() {
        UUID testOrganizationId = UUID.randomUUID();
        Organization testOrganization = new Organization();
        testOrganization.setId(testOrganizationId);

        CreateBoardDto createBoardDto = new CreateBoardDto();
        createBoardDto.setTitle("My Super Evil Inator Plans");
        createBoardDto.setOrganizationId(testOrganizationId);

        Mockito.when(organizationRepository.findById(testOrganizationId)).thenReturn(Optional.of(testOrganization));

        BoardDetailDto result = boardsService.createNewBoard(createBoardDto);
        assert result.getTitle().equals(createBoardDto.getTitle());
    }

    @Test
    void createNewBoard_hasExpectedDefaultColumns() {
        UUID testOrganizationId = UUID.randomUUID();
        Organization testOrganization = new Organization();
        testOrganization.setId(testOrganizationId);

        CreateBoardDto createBoardDto = new CreateBoardDto();
        createBoardDto.setTitle("My Super Evil Inator Plans");
        createBoardDto.setOrganizationId(testOrganizationId);

        Mockito.when(organizationRepository.findById(testOrganizationId)).thenReturn(Optional.of(testOrganization));

        List<String> expectedColumnTitles = List.of("Back Log", "Todo", "In-Progress", "Done");

        BoardDetailDto result = boardsService.createNewBoard(createBoardDto);

        for (int i = 0; i < expectedColumnTitles.size(); i++) {
            String expectedTitle = expectedColumnTitles.get(i);
            BoardColumnDto columnDto = result.getBoardColumns().get(i);

            assert columnDto.getTitle().equals(expectedTitle);

        }
    }

    @Test
    void createNewBoard_hasNoTasks() {
        UUID testOrganizationId = UUID.randomUUID();
        Organization testOrganization = new Organization();
        testOrganization.setId(testOrganizationId);

        CreateBoardDto createBoardDto = new CreateBoardDto();
        createBoardDto.setTitle("My Super Evil Inator Plans");
        createBoardDto.setOrganizationId(testOrganizationId);

        Mockito.when(organizationRepository.findById(testOrganizationId)).thenReturn(Optional.of(testOrganization));

        BoardDetailDto result = boardsService.createNewBoard(createBoardDto);

        for (BoardColumnDto columnDto : result.getBoardColumns()) {
            assert columnDto.getTasks().isEmpty();
        }
    }
}