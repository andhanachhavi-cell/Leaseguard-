import type { Config } from "@netlify/functions";
import { error, getFullState, json } from "../lib/store.js";

export default async (req: Request) => {
  if (req.method !== "GET") return error("Method not allowed", 405);
  return json(await getFullState());
};

export const config: Config = { path: "/api/state" };
