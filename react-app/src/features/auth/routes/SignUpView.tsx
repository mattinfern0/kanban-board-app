import { SignUpFormValues } from "@/features/auth/types";
import { Card, CardContent, CardHeader } from "@mui/material";
import { SignUpForm } from "@/features/auth/components/SignUpForm.tsx";

export const SignUpView = () => {
  const onSubmit = (data: SignUpFormValues) => {
    console.log(data);
  };

  return (
    <Card>
      <CardHeader>Sign Up</CardHeader>
      <CardContent>
        <SignUpForm onSubmit={onSubmit} />
      </CardContent>
    </Card>
  );
};
