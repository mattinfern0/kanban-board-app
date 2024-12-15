import { Controller, useForm } from "react-hook-form";
import { LoginFormValues } from "@/features/auth/types";
import { Button, PasswordInput, Stack, TextInput } from "@mantine/core";

interface Props {
  onSubmit: (data: LoginFormValues) => void;
}

export const LoginForm = (props: Readonly<Props>) => {
  const { onSubmit: propsOnSubmit } = props;

  const formMethods = useForm<LoginFormValues>({
    defaultValues: {
      email: "",
      password: "",
    },
  });

  const { control, handleSubmit } = formMethods;

  const onSubmit = handleSubmit(
    (data) => {
      propsOnSubmit(data);
    },
    (errors) => {
      console.debug(errors);
    },
  );

  return (
    <form onSubmit={onSubmit}>
      <Stack>
        <Controller
          control={control}
          name="email"
          render={({ field, fieldState }) => (
            <TextInput {...field} error={fieldState.error?.message} label="Email" required />
          )}
        />

        <Controller
          control={control}
          name="password"
          render={({ field, fieldState }) => (
            <PasswordInput {...field} error={fieldState.error?.message} label="Password" type="password" required />
          )}
        />

        <Button type="submit" variant="filled" color="primary">
          Login
        </Button>
      </Stack>
    </form>
  );
};
