import { createContext, useContext, useEffect, useMemo, useRef, useState } from "react";
import { useSound } from "./SoundContext.jsx";

const MusicCtx = createContext(null);
export const useMusic = () => useContext(MusicCtx);

export function MusicProvider({
  children,
  mp3 = "/assets/audio/bg_test.mp3",
  ogg = "/assets/audio/bg_test.ogg",
}) {
  const { volume, muted } = useSound();       
  const audioRef = useRef(null);
  const [ready, setReady] = useState(false);

  useEffect(() => {
    if (audioRef.current || typeof Audio === "undefined") return;

    const canUseOgg = (() => {
      try {
        const probe = document.createElement("audio");
        const res = probe?.canPlayType?.('audio/ogg; codecs="vorbis"');
        return res === "probably" || res === "maybe";
      } catch {
        return false;
      }
    })();

    const a = new Audio();
    a.loop = true;
    a.preload = "metadata";        
    a.src = canUseOgg ? ogg : mp3;  
    a.volume = 0;                
    audioRef.current = a;

    return () => {
      try {
        a.pause();
        a.src = ""; 
        a.load();   
      } catch {}
      audioRef.current = null;
    };
  }, [ogg, mp3]);

  useEffect(() => {
    const a = audioRef.current;
    if (!a) return;
    const v = Math.max(0, Math.min(100, Number(volume)));
    a.volume = v / 100;
  }, [volume]);

  useEffect(() => {
    const a = audioRef.current;
    if (!a) return;

    const shouldPlay = !muted && volume > 0;
    if (shouldPlay) {
      a.play()
        .then(() => setReady(true))
        .catch(() => setReady(false)); 
    } else {
      a.pause();
    }
  }, [muted, volume]);

  useEffect(() => {
    const onVis = () => {
      if (document.visibilityState === "visible" && !muted && volume > 0) {
        audioRef.current?.play().catch(() => {});
      }
    };
    document.addEventListener("visibilitychange", onVis);
    return () => document.removeEventListener("visibilitychange", onVis);
  }, [muted, volume]);

  const api = useMemo(
    () => ({
      isReady: ready,
      setTrack: (url) => {
        if (audioRef.current && typeof url === "string" && url.length) {
          audioRef.current.src = url;
          if (!muted && volume > 0) {
            audioRef.current.play().catch(() => {});
          }
        }
      },
      currentTime: () => audioRef.current?.currentTime ?? 0,
      seek: (t) => {
        if (audioRef.current && typeof t === "number") {
          audioRef.current.currentTime = Math.max(0, t);
        }
      },
    }),
    [ready, muted, volume]
  );

  return <MusicCtx.Provider value={api}>{children}</MusicCtx.Provider>;
}
