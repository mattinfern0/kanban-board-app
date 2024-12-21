import { useAuth } from "@/features/auth/components/AuthProvider.tsx";
import { useNavigate } from "react-router";
import React from "react";

export const AuthGuard = ({ children }: { children: React.ReactNode }) => {
  const { user, isInitialized } = useAuth();
  const navigate = useNavigate();

  if (!isInitialized) {
    return null;
  }

  const isAuthenticated = user != null;
  if (!isAuthenticated) {
    navigate("/login");
    return null;
  }
  return children;
};
