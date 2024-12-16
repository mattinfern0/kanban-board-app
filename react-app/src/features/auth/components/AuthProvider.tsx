import React, { createContext, useContext, useEffect, useState } from "react";
import { AuthContextValues, SignUpFormValues } from "@/features/auth/types";
import { firebaseAuth } from "@/features/auth/lib/firebase.ts";
import { signInWithEmailAndPassword, signOut, User as FirebaseUser } from "firebase/auth";

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

  useEffect(() => {
    return firebaseAuth.onAuthStateChanged((firebaseUser) => {
      setUser(firebaseUser);
      setIsInitialized(true);
    });
  }, []);

  const login = async (email: string, password: string) => {
    await signInWithEmailAndPassword(firebaseAuth, email, password);
  };

  const signUp = async (data: SignUpFormValues) => {
    await signInWithEmailAndPassword(firebaseAuth, data.email, data.password1);
  };

  const logout = async () => {
    await signOut(firebaseAuth);
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
