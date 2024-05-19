import { Drawer, List, ListItemButton, ListItemIcon, ListItemText, styled } from "@mui/material";
import { Assignment, ViewColumn } from "@mui/icons-material";
import { Link } from "react-router-dom";

const sidebarWidth = 800;

const DrawerSpacer = styled("div")(({ theme }) => ({
  ...theme.mixins.toolbar,
}));

export const AppSidebar = () => {
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
        <ListItemButton component={Link} to="/boards">
          <ListItemIcon>
            <ViewColumn />
          </ListItemIcon>
          <ListItemText primary="Boards" />
        </ListItemButton>

        <ListItemButton>
          <ListItemIcon>
            <Assignment />
          </ListItemIcon>
          <ListItemText primary="Tasks" />
        </ListItemButton>
      </List>
    </Drawer>
  );
};
