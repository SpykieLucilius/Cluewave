import { createContext, useContext, useMemo, useState, useEffect } from "react";

const SoundContext = createContext(null);

function getNavigationType() {
  try {
    const nav = performance.getEntriesByType("navigation")[0];
    if (nav && nav.type) return nav.type; 
  } catch {}
  try {
    const t = performance?.navigation?.type;
    if (t === 1) return "reload";
    if (t === 2) return "back_forward";
    return "navigate";
  } catch {}
  return "navigate";
}

export function SoundProvider({ children }) {

  const [volume, setVolume] = useState(() => {
    const navType = getNavigationType();
    const hadSession = sessionStorage.getItem("audio_session") === "1";
    const sessionVol = sessionStorage.getItem("sessionVolume");

    if (navType === "reload" && hadSession && sessionVol !== null) {
      const v = Number(sessionVol);
      return Number.isFinite(v) ? Math.max(0, Math.min(100, v)) : 0;
    }

    const consent = localStorage.getItem("audio_consent") === "1";
    if (consent) {
      const lv = Number(localStorage.getItem("lastVolume") ?? 30);
      return Number.isFinite(lv) && lv > 0 ? Math.min(100, Math.max(0, lv)) : 30;
    }
    return 0; 
  });

  const muted = volume === 0;

  const [last, setLast] = useState(() => {
    const lv = Number(localStorage.getItem("lastVolume") ?? 30);
    return Number.isFinite(lv) && lv > 0 ? Math.min(100, Math.max(0, lv)) : 30;
  });

  useEffect(() => {
    sessionStorage.setItem("audio_session", "1");
  }, []);

  useEffect(() => {
    sessionStorage.setItem("sessionVolume", String(volume));
    if (volume > 0) {
      setLast(volume);
      localStorage.setItem("lastVolume", String(volume));
      localStorage.setItem("audio_consent", "1"); 
    }
  }, [volume]);

  const toggleMute = () => {
    if (muted) {
      const v = last || 30;
      setVolume(v);
      localStorage.setItem("audio_consent", "1");
      localStorage.setItem("lastVolume", String(v));
      sessionStorage.setItem("sessionVolume", String(v));
    } else {
      setVolume(0);
      sessionStorage.setItem("sessionVolume", "0");
    }
  };

  const setVolumePct = (v) => {
    const n = Math.max(0, Math.min(100, Number(v)));
    setVolume(n);
  };

  const value = useMemo(
    () => ({ volume, muted, toggleMute, setVolumePct }),
    [volume, muted]
  );

  return <SoundContext.Provider value={value}>{children}</SoundContext.Provider>;
}

export function useSound() {
  const ctx = useContext(SoundContext);
  if (!ctx) throw new Error("useSound must be used within a SoundProvider");
  return ctx;
}
