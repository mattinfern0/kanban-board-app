import { LoginFormValues } from "@/features/auth/types";
import { Alert, Button, Card, Divider, Stack, Title } from "@mantine/core";
import { LoginForm } from "@/features/auth/components/LoginForm.tsx";
import { useLoginMutation } from "@/features/auth/api/login.ts";
import { Link, useNavigate } from "react-router-dom";
import { FirebaseError } from "firebase/app";
import { IconAlertTriangle } from "@tabler/icons-react";

const INVALID_CREDENTIAL_ERROR_CODES = ["auth/invalid-credential", "auth/user-not-found"];

const getLoginErrorMessage = (error: unknown) => {
  if (error instanceof FirebaseError && INVALID_CREDENTIAL_ERROR_CODES.includes(error.code)) {
    return "Invalid email or password";
  }

  return "An uknown error occurred";
};

export const LoginView = () => {
  const loginMutation = useLoginMutation();
  const navigate = useNavigate();

  const onSubmit = (data: LoginFormValues) => {
    if (loginMutation.isPending) {
      return;
    }

    loginMutation.mutate(data, {
      onSuccess: () => {
        console.log("Success");
        navigate("/boards");
      },
      onError: (error) => {
        console.error(error);
      },
    });
  };

  const errorMessage = loginMutation.error ? getLoginErrorMessage(loginMutation.error) : null;

  return (
    <Card withBorder>
      <Stack>
        <Title order={2}>Login</Title>
        {errorMessage && <Alert variant="filled" color="danger" icon={<IconAlertTriangle />} title={errorMessage} />}
        <LoginForm onSubmit={onSubmit} />

        <Divider label="Or" />

        <Button component={Link} to="/sign-up" color="secondary" variant="outline">
          Sign Up
        </Button>
      </Stack>
    </Card>
  );
};
