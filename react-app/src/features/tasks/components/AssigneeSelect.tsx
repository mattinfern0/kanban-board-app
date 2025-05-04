import { Avatar, CheckIcon, Combobox, Group, Input, Pill, PillsInput, Stack, useCombobox } from "@mantine/core";

interface AssigneeOption {
  id: string;
  firstName: string;
  lastName: string;
}

export interface AssigneeSelectProps {
  inputLabel: string;
  value: string[];
  assigneeOptions: AssigneeOption[];
  onChange: (value: string[]) => void;
  onBlur: () => void;
}

export function AssigneeSelect(props: Readonly<AssigneeSelectProps>) {
  const { assigneeOptions, value, onChange, onBlur } = props;

  const combobox = useCombobox({
    onDropdownClose: () => combobox.resetSelectedOption(),
    onDropdownOpen: () => combobox.updateSelectedOptionIndex("active"),
  });

  const usersById: Record<string, AssigneeOption> = {};
  assigneeOptions.forEach((user) => {
    usersById[user.id] = user;
  });

  const handleValueSelect = (option: string) =>
    onChange(value.includes(option) ? value.filter((v) => v !== option) : [...value, option]);

  const handleValueRemove = (option: string) => onChange(value.filter((v) => v !== option));

  const sortedValues = value.sort((a, b) => {
    const userA = usersById[a];
    const userB = usersById[b];

    const firstNameComparison = userA.firstName.localeCompare(userB.firstName);
    if (firstNameComparison !== 0) {
      return firstNameComparison;
    }
    return userA.lastName.localeCompare(userB.lastName);
  });

  const valueElements = [];
  for (const item of sortedValues) {
    const user = usersById[item];
    if (!user) {
      continue;
    }
    const name = `${user.firstName} ${user.lastName}`;

    valueElements.push(
      <Pill key={item} onRemove={() => handleValueRemove(item)} styles={{ root: { paddingLeft: 0 } }}>
        <Group align="center">
          <Avatar name={name} color="initials" />
          {name}
        </Group>
      </Pill>,
    );
  }

  const options = assigneeOptions.map((item) => {
    return (
      <Combobox.Option value={item.id} key={item.id} active={value.includes(item.id)}>
        <Group gap="sm">
          {value.includes(item.id) ? <CheckIcon size={12} /> : null}
          <span>
            {item.firstName} {item.lastName}
          </span>
        </Group>
      </Combobox.Option>
    );
  });

  const onInputBlur = () => {
    combobox.closeDropdown();
    onBlur();
  };

  return (
    <Combobox store={combobox} onOptionSubmit={handleValueSelect} withinPortal={false}>
      <Combobox.DropdownTarget>
        <PillsInput pointer onClick={() => combobox.toggleDropdown()}>
          <Pill.Group>
            {valueElements.length > 0 ? (
              <Stack>{valueElements}</Stack>
            ) : (
              <Input.Placeholder>No Assignees</Input.Placeholder>
            )}

            <Combobox.EventsTarget>
              <PillsInput.Field
                type="hidden"
                onBlur={onInputBlur}
                onKeyDown={(event) => {
                  if (event.key === "Backspace") {
                    event.preventDefault();
                    handleValueRemove(value[value.length - 1]);
                  }
                }}
              />
            </Combobox.EventsTarget>
          </Pill.Group>
        </PillsInput>
      </Combobox.DropdownTarget>

      <Combobox.Dropdown>
        <Combobox.Options>{options}</Combobox.Options>
      </Combobox.Dropdown>
    </Combobox>
  );
}
