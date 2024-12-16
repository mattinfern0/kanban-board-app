import { LoginFormValues } from "@/features/auth/types";
import { Alert, Card, Title } from "@mantine/core";
import { LoginForm } from "@/features/auth/components/LoginForm.tsx";
import { useLoginMutation } from "@/features/auth/api/login.ts";
import { useNavigate } from "react-router-dom";
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
      <Title order={3} mb="1rem">
        Login
      </Title>
      {errorMessage && (
        <Alert variant="filled" color="danger" icon={<IconAlertTriangle />} title={errorMessage} mb="1rem" />
      )}
      <LoginForm onSubmit={onSubmit} />
    </Card>
  );
};
