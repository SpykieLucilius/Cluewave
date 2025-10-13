import { createContext, useContext, useState, useEffect, useMemo } from 'react';

const SoundContext = createContext(null);

export function SoundProvider({ children }) {
    const [volume, setVolume] = useState(() => {
        const v = localStorage.getItem('volume');
        return v !== null ? Number(v) : 30;
    });
    const [muted, setMuted] = useState(() => volume === 0);
    const [last, setLast] = useState(volume || 30);

    useEffect(() => {
        localStorage.setItem('volume', String(volume));
    }, [volume]);

    const toggleMute = () => {
        setMuted(m => {
            if (m) { setVolume(last || 30); return false; }
            setLast(volume);
            setVolume(0);
            return true;
        });
    };

    const setVolumePct = (v) => {
        const n = Math.max(0, Math.min(100, Number(v)));
        setVolume(n);
        setMuted(n === 0);
    };

    const value = useMemo(() => ({ volume, muted, toggleMute, setVolumePct }), [volume, muted]);

    return <SoundContext.Provider value={value}>{children}</SoundContext.Provider>;
}

export function useSound() {
    const ctx = useContext(SoundContext);
    if (!ctx) throw new Error('useSound must be used within a SoundProvider');
    return ctx;
}