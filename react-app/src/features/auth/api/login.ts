import { useAuth } from "@/features/auth/components/AuthProvider.tsx";
import { useMutation } from "@tanstack/react-query";

export const useLoginMutation = () => {
  const auth = useAuth();
  return useMutation({
    mutationFn: async (args: { email: string; password: string }) => {
      return await auth.login(args.email, args.password);
    },
  });
};
