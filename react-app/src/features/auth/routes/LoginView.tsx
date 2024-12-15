import { LoginFormValues } from "@/features/auth/types";
import { Card, Title } from "@mantine/core";
import { LoginForm } from "@/features/auth/components/LoginForm.tsx";
import { useLoginMutation } from "@/features/auth/api/login.ts";
import { useNavigate } from "react-router-dom";

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

  return (
    <Card withBorder>
      <Title order={3} mb="1rem">
        Login
      </Title>
      <LoginForm onSubmit={onSubmit} />
    </Card>
  );
};
