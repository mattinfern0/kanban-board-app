package io.mattinfern0.kanbanboardapi.core.entities;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
class BoardColumnUnitTest {

    @Test
    void setBoard_setsProperty() {
        Board testBoard = new Board();
        BoardColumn testColumn = new BoardColumn();

        testColumn.setBoard(testBoard);
        assert testColumn.getBoard().equals(testBoard);
    }

    @Test
    void setBoard_addsColumnToBoardColumnList_boardNotNull() {
        Board testBoard = new Board();
        BoardColumn testColumn = new BoardColumn();

        testColumn.setBoard(testBoard);
        assert testBoard.getBoardColumns().contains(testColumn);
    }

    @Test
    void setBoard_removesColumnFromOldBoardColumnList_oldBoardNotNull() {
        Board testBoard = new Board();
        Board oldBoard = new Board();
        BoardColumn testColumn = new BoardColumn();
        testColumn.setBoard(oldBoard);
        testColumn.setBoard(testBoard);
        assert !oldBoard.getBoardColumns().contains(testColumn);
    }
}