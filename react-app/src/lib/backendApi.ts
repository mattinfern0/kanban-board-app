import ky from "ky";

const backendUrl = "/api";

export const client = ky.extend({
  prefixUrl: backendUrl,
});
