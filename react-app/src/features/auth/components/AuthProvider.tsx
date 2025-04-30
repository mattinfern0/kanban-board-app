import React, { createContext, useContext, useEffect, useState } from "react";
import { AuthContextValues, SignUpFormValues } from "@/features/auth/types";
import { firebaseAuth } from "@/features/auth/lib/firebase.ts";
import {
  createUserWithEmailAndPassword,
  deleteUser,
  getAuth,
  signInWithEmailAndPassword,
  signOut,
  User as FirebaseUser,
} from "firebase/auth";
import { backendSignup } from "@/features/auth/api/sign-up.ts";
import { useQueryClient } from "@tanstack/react-query";

const AuthContext = createContext<AuthContextValues>({
  user: null,
  isInitialized: false,
  signUp: async () => Promise.resolve(),
  login: async () => Promise.resolve(),
  logout: async () => Promise.resolve(),
});

export const AuthProvider = ({ children }: { children?: React.ReactNode }) => {
  const [user, setUser] = useState<FirebaseUser | null>(null);
  const [isInitialized, setIsInitialized] = useState<boolean>(false);
  const queryClient = useQueryClient();

  useEffect(() => {
    return firebaseAuth.onAuthStateChanged(async (firebaseUser) => {
      console.debug("new user state", firebaseUser);
      const idToken = await firebaseUser?.getIdToken();
      console.debug("idToken", `"${idToken}"`);
      setUser(firebaseUser);
      setIsInitialized(true);
    });
  }, []);

  const login = async (email: string, password: string) => {
    await signInWithEmailAndPassword(firebaseAuth, email, password);
  };

  const signUp = async (data: SignUpFormValues) => {
    await createUserWithEmailAndPassword(firebaseAuth, data.email, data.password1);

    try {
      await backendSignup({
        firstName: data.firstName,
        lastName: data.lastName,
      });
    } catch (e) {
      console.log("Backend sign-up failed. Rolling back firebase user creation");
      const auth = getAuth();
      if (auth && auth.currentUser) {
        await deleteUser(auth.currentUser);
      }
      throw e;
    }
  };

  const logout = async () => {
    await signOut(firebaseAuth);
    queryClient.clear();
  };

  const values: AuthContextValues = {
    user: user,
    isInitialized: isInitialized,
    signUp: signUp,
    login: login,
    logout: logout,
  };
  return <AuthContext.Provider value={values}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  return useContext(AuthContext);
};
