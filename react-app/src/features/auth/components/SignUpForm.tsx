import { Controller, useForm, UseFormReturn } from "react-hook-form";
import { SignUpFormSchema, SignUpFormValues } from "@/features/auth/types";
import { zodResolver } from "@hookform/resolvers/zod";
import { Button, Stack, TextField } from "@mui/material";

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
      <Stack spacing={3}>
        <Controller
          control={control}
          name="email"
          render={({ field, fieldState }) => (
            <TextField {...field} error={!!fieldState.error} helperText={fieldState.error?.message} label="Email" />
          )}
        />

        <Controller
          control={control}
          name="password1"
          render={({ field, fieldState }) => (
            <TextField
              {...field}
              error={!!fieldState.error}
              helperText={fieldState.error?.message}
              label="Password"
              type="password"
            />
          )}
        />

        <Controller
          control={control}
          name="password2"
          render={({ field, fieldState }) => (
            <TextField
              {...field}
              error={!!fieldState.error}
              helperText={fieldState.error?.message}
              label="Confirm Password"
              type="password"
            />
          )}
        />

        <Controller
          control={control}
          name="firstName"
          render={({ field, fieldState }) => (
            <TextField
              {...field}
              error={!!fieldState.error}
              helperText={fieldState.error?.message}
              label="First Name"
            />
          )}
        />

        <Controller
          control={control}
          name="lastName"
          render={({ field, fieldState }) => (
            <TextField {...field} error={!!fieldState.error} helperText={fieldState.error?.message} label="Last Name" />
          )}
        />

        <Button type="submit" variant="contained" size="large">
          Sign Up
        </Button>
      </Stack>
    </form>
  );
};
