import { useState } from 'react';

export function useSoundControl(initial = 30) {
    const [volume, setVolume] = useState(initial);
    const [muted, setMuted] = useState(initial === 0);
    const [last, setLast] = useState(initial);

    const onToggle = () => {
        setMuted(m => {
            if (m) { 
                setVolume(last || 30); return false; }
            setLast(volume);
            setVolume(0);
            return true;
        });
    };
    
    const onChange = (e) => {
        const v = Number(e.target.value);
        setVolume(v);
        setMuted(v === 0);
    };

    return { muted, volume, onToggle, onChange };
}