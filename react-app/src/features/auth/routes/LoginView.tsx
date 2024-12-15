import { LoginFormValues } from "@/features/auth/types";
import { Card, Title } from "@mantine/core";
import { LoginForm } from "@/features/auth/components/LoginForm.tsx";

export const LoginView = () => {
  const onSubmit = (data: LoginFormValues) => {
    console.log(data);
  };

  return (
    <Card withBorder>
      <Title order={3} mb="1rem">
        Login
      </Title>
      <LoginForm onSubmit={onSubmit} />
    </Card>
  );
};
