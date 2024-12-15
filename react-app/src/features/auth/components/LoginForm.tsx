import { Controller, useForm } from "react-hook-form";
import { LoginFormSchema, LoginFormValues } from "@/features/auth/types";
import { Button, PasswordInput, Stack, TextInput } from "@mantine/core";
import { zodResolver } from "@hookform/resolvers/zod";

interface Props {
  onSubmit: (data: LoginFormValues) => void;
  isSubmitting?: boolean;
}

export const LoginForm = (props: Readonly<Props>) => {
  const { onSubmit: propsOnSubmit } = props;

  const formMethods = useForm<LoginFormValues>({
    defaultValues: {
      email: "",
      password: "",
    },
    resolver: zodResolver(LoginFormSchema),
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

        <Button type="submit" variant="filled" color="primary" disabled={props.isSubmitting}>
          Login
        </Button>
      </Stack>
    </form>
  );
};
