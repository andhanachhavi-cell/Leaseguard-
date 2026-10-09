import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./styles.css";

function App() {
  return (
    <main>
      <div className="eyebrow">Commercial Real Estate Intelligence</div>
      <h1 className="page-title">LeaseGuard <em>AI</em></h1>
      <p className="lede">AI lease vault, CPI escalation modeling and COI compliance tracking. The full web dashboard is coming soon.</p>
    </main>
  );
}

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <App />
  </StrictMode>,
);
