import { SignUpFormValues } from "@/features/auth/types";
import { SignUpForm } from "@/features/auth/components/SignUpForm.tsx";
import { Card, Title } from "@mantine/core";

export const SignUpView = () => {
  const onSubmit = (data: SignUpFormValues) => {
    console.log(data);
  };

  return (
    <Card withBorder>
      <Title order={3} mb="1rem">
        Sign Up
      </Title>
      <SignUpForm onSubmit={onSubmit} />
    </Card>
  );
};
