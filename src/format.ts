export const usd = (n: number, digits = 2) =>
  "$" + n.toLocaleString("en-US", { minimumFractionDigits: digits, maximumFractionDigits: digits });

export const int = (n: number) => Math.round(n).toLocaleString("en-US");

export const pct = (n: number, digits = 1) => `${n.toFixed(digits)}%`;

export const time = (iso: string) =>
  new Date(iso).toLocaleTimeString("en-US", { hour12: false, hour: "2-digit", minute: "2-digit", second: "2-digit" });

export const MONTHS = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

/** 12-month CPI projection, matching the original app's escalation model. */
export function cpiProjection(annualRent: number, cpiRate: number, cap: number) {
  const effectiveRate = Math.min(cpiRate, cap);
  const monthlyBase = annualRent / 12;
  const monthlyDelta = (annualRent * (effectiveRate / 100)) / 12;
  return {
    effectiveRate,
    annualDelta: annualRent * (effectiveRate / 100),
    capSavings: cpiRate > cap ? annualRent * ((cpiRate - cap) / 100) : 0,
    months: MONTHS.map((name, i) => ({
      monthNumber: i + 1,
      monthName: name,
      projectedRent: monthlyBase + monthlyDelta * ((i + 1) / 12),
      inflationDelta: monthlyDelta * (i + 1),
    })),
  };
}
