import { Controller, useForm, UseFormReturn } from "react-hook-form";
import { SignUpFormSchema, SignUpFormValues } from "@/features/auth/types";
import { zodResolver } from "@hookform/resolvers/zod";
import { Button, PasswordInput, Stack, TextInput } from "@mantine/core";

interface UseSignUpFormReturn {
  formMethods: UseFormReturn<SignUpFormValues>;
}

const UseSignUpForm = (): UseSignUpFormReturn => {
  const formMethods = useForm<SignUpFormValues>({
    resolver: zodResolver(SignUpFormSchema),
  });

  return {
    formMethods,
  };
};

interface SignUpFormProps {
  onSubmit: (data: SignUpFormValues) => void;
}

export const SignUpForm = (props: SignUpFormProps) => {
  const { onSubmit: propsOnSubmit } = props;
  const { formMethods } = UseSignUpForm();
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
          name="password1"
          render={({ field, fieldState }) => (
            <PasswordInput {...field} error={fieldState.error?.message} label="Password" type="password" required />
          )}
        />

        <Controller
          control={control}
          name="password2"
          render={({ field, fieldState }) => (
            <PasswordInput
              {...field}
              error={fieldState.error?.message}
              label="Confirm Password"
              type="password"
              required
            />
          )}
        />

        <Controller
          control={control}
          name="firstName"
          render={({ field, fieldState }) => (
            <TextInput {...field} error={fieldState.error?.message} label="First Name" required />
          )}
        />

        <Controller
          control={control}
          name="lastName"
          render={({ field, fieldState }) => (
            <TextInput {...field} error={fieldState.error?.message} label="Last Name" required />
          )}
        />

        <Button type="submit" variant="filled" size="lg">
          Sign Up
        </Button>
      </Stack>
    </form>
  );
};
