import { SignUpFormValues } from "@/features/auth/types";
import { SignUpForm } from "@/features/auth/components/SignUpForm.tsx";
import { Alert, Button, Card, Divider, Stack, Title } from "@mantine/core";
import { Link, useNavigate } from "react-router";
import { useSignUpMutation } from "@/features/auth/api/sign-up.ts";
import { IconAlertTriangle } from "@tabler/icons-react";
import { FirebaseError } from "firebase/app";

const firebaseErrorCodeToMessage: Record<string, string> = {
  "auth/email-already-in-use": "An account with this email already exists.",
};

const getSignUpErrorMessage = (error: unknown) => {
  if (error instanceof FirebaseError) {
    return firebaseErrorCodeToMessage[error.code] ?? "An uknown error occurred";
  }

  return "An uknown error occurred";
};

export const SignUpView = () => {
  const navigate = useNavigate();
  const signUpMutation = useSignUpMutation();

  const onSubmit = (data: SignUpFormValues) => {
    console.log(data);

    signUpMutation.mutate(data, {
      onSuccess: () => {
        navigate("/boards");
      },
      onError: (error) => {
        console.error(error);
      },
    });
  };

  const errorMessage: string | null = signUpMutation.error ? getSignUpErrorMessage(signUpMutation.error) : null;

  return (
    <Card withBorder>
      <Stack>
        <Title order={2}>Sign Up</Title>
        {errorMessage && <Alert variant="filled" color="danger" icon={<IconAlertTriangle />} title={errorMessage} />}
        <SignUpForm onSubmit={onSubmit} />
        <Divider label="Or" />

        <Button component={Link} to="/login" color="secondary" variant="outline">
          Login
        </Button>
      </Stack>
    </Card>
  );
};
