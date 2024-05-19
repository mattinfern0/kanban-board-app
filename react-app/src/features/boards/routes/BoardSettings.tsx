import { Box, Button, Card, CardContent, Stack, Tab, Typography } from "@mui/material";
import { Link, useParams } from "react-router-dom";
import { useBoardQuery } from "@/features/boards/apis/getBoard.ts";
import React, { useState } from "react";
import { TabContext, TabList, TabPanel } from "@mui/lab";
import { ChevronLeft } from "@mui/icons-material";

type TabValue = "settings" | "danger";

export const BoardSettings = () => {
  const { boardId } = useParams();
  const boardQuery = useBoardQuery(boardId || "");
  const [activeTab, setActiveTab] = useState<TabValue>("settings");

  let element;

  if (boardQuery.isLoading) {
    element = <Typography>Loading...</Typography>;
  } else if (boardQuery.isError) {
    element = <Typography>Error loading board</Typography>;
  } else if (boardQuery.isSuccess) {
    const handleTabChange = (_event: React.SyntheticEvent, newValue: TabValue) => {
      setActiveTab(newValue);
    };

    element = (
      <>
        <Card>
          <CardContent>
            <TabContext value={activeTab}>
              <Box sx={{ borderBottom: 1, borderColor: "divider" }}>
                <TabList onChange={handleTabChange}>
                  <Tab label="Info" value="settings" />
                  <Tab label="Danger" value="danger" />
                </TabList>
              </Box>
              <TabPanel value="settings">Update Board Settings Form</TabPanel>

              <TabPanel value="danger">
                <Button variant="outlined" color="error">
                  Delete Board
                </Button>
              </TabPanel>
            </TabContext>
          </CardContent>
        </Card>
      </>
    );
  }

  return (
    <>
      <Stack direction="row" mb={3} alignItems="center" spacing={3}>
        <Button
          component={Link}
          to={`/boards/${boardId}`}
          startIcon={<ChevronLeft />}
          variant="contained"
          sx={{ mb: 3 }}
        >
          Back
        </Button>
        <Typography variant="h4">Board Settings</Typography>
      </Stack>
      {element}
    </>
  );
};
