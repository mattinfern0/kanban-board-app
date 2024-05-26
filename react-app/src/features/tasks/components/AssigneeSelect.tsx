import { Theme, useTheme } from "@mui/material/styles";
import OutlinedInput from "@mui/material/OutlinedInput";
import MenuItem from "@mui/material/MenuItem";
import FormControl from "@mui/material/FormControl";
import Select, { SelectChangeEvent } from "@mui/material/Select";
import Chip from "@mui/material/Chip";
import { Stack } from "@mui/material";

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
  inputLabel: string;
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
      <Select
        id={props.inputLabel}
        multiple
        value={props.value}
        onChange={handleChange}
        onBlur={props.onBlur}
        input={<OutlinedInput />}
        renderValue={(selected) => (
          <Stack spacing={1}>
            {selected.map((value) => (
              <Chip key={value} label={valueToLabel[value]} />
            ))}
          </Stack>
        )}
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
