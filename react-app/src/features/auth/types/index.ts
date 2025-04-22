import { z } from "zod";
import { User as FirebaseUser } from "firebase/auth";

const MIN_PASSWORD_LENGTH = 8;

export const SignUpFormSchema = z
  .object({
    email: z.string().min(1, "Required").email("Invalid email format"),
    password1: z.string().min(8, `Must be at least ${MIN_PASSWORD_LENGTH} characters`),
    password2: z.string(),

    firstName: z.string().min(1, "Required"),
    lastName: z.string().min(1, "Required"),
  })
  .refine((data) => data.password1 === data.password2, {
    message: "Passwords don't match",
    path: ["password2"],
  });

export type SignUpFormValues = z.infer<typeof SignUpFormSchema>;

export const LoginFormSchema = z.object({
  email: z.string().email("Invalid email"),
  password: z.string().min(1, "Required"),
});

export type LoginFormValues = z.infer<typeof LoginFormSchema>;

export interface AuthContextValues {
  user: FirebaseUser | null;
  isInitialized: boolean;
  signUp: (data: SignUpFormValues) => Promise<void>;
  login: (email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
}

export interface SignUpRequestBody {
  firstName: string;
  lastName: string;
}
