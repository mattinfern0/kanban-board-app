import { Divider, Drawer, List, ListItem, ListItemButton, ListItemText, styled } from "@mui/material";
import { useBoardListQuery } from "@/features/boards/apis/getBoardList.ts";
import { BoardSummary } from "@/features/boards/types";
import { Link } from "react-router-dom";

const sidebarWidth = 800;

const DrawerSpacer = styled("div")(({ theme }) => ({
  ...theme.mixins.toolbar,
}));

export const AppSidebar = () => {
  const boardListQuery = useBoardListQuery();
  const boards: BoardSummary[] = boardListQuery.data || [];

  const boardListButtons = boards.map((board) => (
    <ListItemButton key={board.id} component={Link} to={`/boards/${board.id}`}>
      <ListItemText primary={board.title} />
    </ListItemButton>
  ));

  return (
    <Drawer
      variant="persistent"
      anchor="left"
      open={true}
      sx={{ width: sidebarWidth, flexShrink: 0, zIndex: -1 }}
      PaperProps={{
        sx: {
          backgroundColor: "#dadada",
          color: "#000000",
        },
      }}
    >
      <DrawerSpacer />
      <List>
        <ListItemButton>
          <ListItemText primary="All Tasks" />
        </ListItemButton>

        <Divider />

        <ListItem>
          <ListItemText>Boards</ListItemText>
        </ListItem>

        {boardListButtons}
      </List>
    </Drawer>
  );
};
