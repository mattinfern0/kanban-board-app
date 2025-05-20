import { Avatar, createTheme, MantineColorsTuple, virtualColor } from "@mantine/core";

const blueTeal: MantineColorsTuple = [
  "#e2fbff",
  "#d3f1f7",
  "#aee0ea",
  "#85cedd",
  "#63bed1",
  "#4cb5cb",
  "#3cb0c8",
  "#299bb1",
  "#158a9f",
  "#00788c",
];

const corkBrown: MantineColorsTuple = [
  "#ffefe9",
  "#f8dfd8",
  "#e9beb2",
  "#db9b89",
  "#cf7d66",
  "#c86a4f",
  "#bf583b",
  "#ae4f35",
  "#9c452d",
  "#8a3923",
];

const darkBrown: MantineColorsTuple = [
  "#f7f4f3",
  "#e6e6e6",
  "#cdcbca",
  "#b5adab",
  "#a19490",
  "#95847f",
  "#917b75",
  "#7e6963",
  "#715d57",
  "#4d3c37",
];

export const theme = createTheme({
  colors: {
    blueTeal: blueTeal,
    corkBrown: corkBrown,
    darkBrown: darkBrown,
    primary: virtualColor({
      name: "primary",
      light: "blueTeal",
      dark: "blue",
    }),
    secondary: virtualColor({
      name: "secondary",
      light: "gray",
      dark: "gray",
    }),
    danger: virtualColor({
      name: "danger",
      light: "red",
      dark: "red",
    }),
    success: virtualColor({
      name: "success",
      light: "green",
      dark: "green",
    }),
    stickyNote: virtualColor({
      name: "stickyNote",
      light: "yellow",
      dark: "yellow",
    }),
  },
  primaryColor: "blueTeal",
  shadows: {
    paper: "3px 3px 5px rgba(0,0,0,0.5)",
  },
  components: {
    Avatar: Avatar.extend({
      defaultProps: {
        variant: "filled",
      },
    }),
  },
});
