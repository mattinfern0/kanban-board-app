import { SignUpFormValues } from "@/features/auth/types";
import { SignUpForm } from "@/features/auth/components/SignUpForm.tsx";
import { Button, Card, Divider, Stack, Title } from "@mantine/core";
import { Link, useNavigate } from "react-router";
import { useSignUpMutation } from "@/features/auth/api/sign-up.ts";

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

  return (
    <Card withBorder>
      <Stack>
        <Title order={2}>Sign Up</Title>
        <SignUpForm onSubmit={onSubmit} />
        <Divider label="Or" />

        <Button component={Link} to="/login" color="secondary" variant="outline">
          Login
        </Button>
      </Stack>
    </Card>
  );
};
