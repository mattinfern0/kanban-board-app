import { useAuth } from "@/features/auth/components/AuthProvider.tsx";
import { useMutation } from "@tanstack/react-query";
import { SignUpFormValues, SignUpRequestBody } from "@/features/auth/types";
import { client } from "@/lib/backendApi.ts";

export const backendSignup = async (body: SignUpRequestBody) => {
  return await client
    .post(`users/sign-up`, {
      json: body,
    })
    .json();
};

export const useSignUpMutation = () => {
  const auth = useAuth();
  return useMutation({
    mutationFn: async (args: SignUpFormValues) => {
      await auth.signUp(args);
    },
  });
};
