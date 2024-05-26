import { Theme, useTheme } from "@mui/material/styles";
import Box from "@mui/material/Box";
import OutlinedInput from "@mui/material/OutlinedInput";
import InputLabel from "@mui/material/InputLabel";
import MenuItem from "@mui/material/MenuItem";
import FormControl from "@mui/material/FormControl";
import Select, { SelectChangeEvent } from "@mui/material/Select";
import Chip from "@mui/material/Chip";

const ITEM_HEIGHT = 48;
const ITEM_PADDING_TOP = 8;
const MenuProps = {
  PaperProps: {
    style: {
      maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
      width: 250,
    },
  },
};

function getStyles(name: string, personName: readonly string[], theme: Theme) {
  return {
    fontWeight:
      personName.indexOf(name) === -1 ? theme.typography.fontWeightRegular : theme.typography.fontWeightMedium,
  };
}

export interface AssigneeSelectOption {
  value: string;
  label: string;
}

export interface AssigneeSelectProps {
  labelId: string;
  value: string[];
  options: AssigneeSelectOption[];
  onChange: (value: string[]) => void;
  onBlur: () => void;
}

export const AssigneeSelect = (props: AssigneeSelectProps) => {
  const theme = useTheme();

  const handleChange = (event: SelectChangeEvent<string[]>) => {
    const {
      target: { value },
    } = event;
    const parsedValue = typeof value === "string" ? value.split(",") : value;
    props.onChange(parsedValue);
  };

  const valueToLabel: Record<string, string> = {};
  props.options.forEach((option) => {
    valueToLabel[option.value] = option.label;
  });

  return (
    <FormControl>
      <InputLabel id={props.labelId}>Assignees</InputLabel>
      <Select
        labelId={props.labelId}
        multiple
        value={props.value}
        onChange={handleChange}
        onBlur={props.onBlur}
        input={<OutlinedInput label="Chip" />}
        renderValue={(selected) => (
          <Box sx={{ display: "flex", flexWrap: "wrap", gap: 0.5 }}>
            {selected.map((value) => (
              <Chip key={value} label={valueToLabel[value]} />
            ))}
          </Box>
        )}
        MenuProps={MenuProps}
      >
        {props.options.map((option) => (
          <MenuItem key={option.value} value={option.value} style={getStyles(option.label, props.value, theme)}>
            {option.label}
          </MenuItem>
        ))}
      </Select>
    </FormControl>
  );
};
