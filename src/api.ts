import type { AcquisitionLead, AppState, CoiItem, Lease, Portfolio } from "./types";

async function request<T>(url: string, init?: RequestInit): Promise<T> {
  const res = await fetch(url, {
    ...init,
    headers: { "Content-Type": "application/json", ...init?.headers },
  });
  const data = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(data?.error || `Request failed (${res.status})`);
  return data as T;
}

const post = <T>(url: string, body: unknown, method = "POST") =>
  request<T>(url, { method, body: JSON.stringify(body) });

export const api = {
  state: () => request<AppState>("/api/state"),
  updatePortfolio: (portfolioSizeSf: number, rentalWeightPerSf: number, officer: string) =>
    post<Portfolio>("/api/portfolio", { portfolioSizeSf, rentalWeightPerSf, officer }, "PUT"),
  parseLease: (payload: { documentName: string; text?: string; file?: { mimeType: string; data: string } }) =>
    post<Lease>("/api/leases/parse", payload),
  updateCoi: (id: string, action: "deficiency" | "verify", officer: string) =>
    post<CoiItem>(`/api/coi/${encodeURIComponent(id)}`, { action, officer }),
  audit: (action: string, officer: string) => post<{ ok: true }>("/api/audit", { action, officer }),
  submitLead: (payload: { officerName: string; email: string; scale: string; intentVerified: boolean }) =>
    post<AcquisitionLead>("/api/leads", payload),
};
