import { Theme, useTheme } from "@mui/material/styles";
import OutlinedInput from "@mui/material/OutlinedInput";
import MenuItem from "@mui/material/MenuItem";
import FormControl from "@mui/material/FormControl";
import Select, { SelectChangeEvent } from "@mui/material/Select";
import Chip from "@mui/material/Chip";
import { Avatar, Stack } from "@mui/material";
import { stringToColor } from "@/lib/utils.ts";

function getStyles(name: string, personName: readonly string[], theme: Theme) {
  return {
    fontWeight:
      personName.indexOf(name) === -1 ? theme.typography.fontWeightRegular : theme.typography.fontWeightMedium,
  };
}

interface AssigneeOption {
  id: string;
  firstName: string;
  lastName: string;
}

export interface SelectOption {
  value: string;
  label: string;
}

const UserAvatar = (props: { user: AssigneeOption }) => {
  return (
    <Avatar sx={{ width: 32, height: 32, fontSize: "1rem", bgcolor: stringToColor(props.user.id) }}>
      {props.user.firstName[0]}
      {props.user.lastName[0]}
    </Avatar>
  );
};

export interface AssigneeSelectProps {
  inputLabel: string;
  value: string[];
  assigneeOptions: AssigneeOption[];
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

  const selectOptions: SelectOption[] = props.assigneeOptions.map((assignee) => ({
    value: assignee.id,
    label: `${assignee.firstName} ${assignee.lastName}`,
  }));

  const usersById: Record<string, AssigneeOption> = {};
  props.assigneeOptions.forEach((user) => {
    usersById[user.id] = user;
  });

  const valueToLabel: Record<string, string> = {};
  selectOptions.forEach((option) => {
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
          <Stack spacing={1} alignItems="flex-start">
            {selected.map((value) => (
              <Chip key={value} avatar={<UserAvatar user={usersById[value]} />} label={valueToLabel[value]} />
            ))}
          </Stack>
        )}
      >
        {selectOptions.map((option) => (
          <MenuItem key={option.value} value={option.value} style={getStyles(option.label, props.value, theme)}>
            <UserAvatar user={usersById[option.value]} />
            <span style={{ marginLeft: "1rem" }}>{option.label}</span>
          </MenuItem>
        ))}
      </Select>
    </FormControl>
  );
};
