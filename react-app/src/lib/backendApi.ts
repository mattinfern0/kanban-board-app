import ky from "ky";
import { firebaseAuth } from "@/features/auth/lib/firebase.ts";

const backendUrl = "/api";

export const client = ky.extend({
  prefixUrl: backendUrl,
  hooks: {
    beforeRequest: [
      async (request) => {
        const token = (await firebaseAuth.currentUser?.getIdToken()) || null;
        if (token) {
          request.headers.set("Authorization", `Bearer ${token}`);
        } else {
          request.headers.delete("Authorization");
        }
      },
    ],
  },
});
