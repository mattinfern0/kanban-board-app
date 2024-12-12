import { createTheme, MantineColorsTuple, virtualColor } from "@mantine/core";

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

export const theme = createTheme({
  colors: {
    blueTeal: blueTeal,
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
  },
});
