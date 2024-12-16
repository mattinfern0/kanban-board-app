import { initializeApp } from "firebase/app";
import { connectAuthEmulator, getAuth } from "firebase/auth";

const firebaseConfig = {
  // Firebase docs say it's safe to expose this API key. https://firebase.google.com/docs/projects/api-keys
  // Instead use Firebase rules to secure the data.
  apiKey: "AIzaSyBMedGF42YKpqlFg1_dBadpW_oOkRk68C8",
  authDomain: "kanban-app-b5cd2.firebaseapp.com",
  projectId: "kanban-app-b5cd2",
  storageBucket: "kanban-app-b5cd2.firebasestorage.app",
  messagingSenderId: "667573382391",
  appId: "1:667573382391:web:ab7c4f01c861ed144af7f2",
};

const authEmulatorUrl: string | null = import.meta.env.VITE_FIREBASE_AUTH_EMULATOR_URL;
const useAuthEmulator = authEmulatorUrl != null;

export const firebaseApp = initializeApp(firebaseConfig);
export const firebaseAuth = getAuth(firebaseApp);

if (useAuthEmulator) {
  connectAuthEmulator(firebaseAuth, authEmulatorUrl);
}
