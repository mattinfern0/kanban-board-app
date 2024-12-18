import { SignUpFormValues } from "@/features/auth/types";
import { SignUpForm } from "@/features/auth/components/SignUpForm.tsx";
import { Button, Card, Divider, Stack, Title } from "@mantine/core";
import { Link } from "react-router-dom";

export const SignUpView = () => {
  const onSubmit = (data: SignUpFormValues) => {
    console.log(data);
  };

  return (
    <Card withBorder>
      <Stack>
        <Title order={3}>Sign Up</Title>
        <SignUpForm onSubmit={onSubmit} />
        <Divider label="Or" />

        <Button component={Link} to="/login" color="secondary" variant="outline">
          Login
        </Button>
      </Stack>
    </Card>
  );
};
