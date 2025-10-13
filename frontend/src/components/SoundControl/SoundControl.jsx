export default function SoundControl({ muted, volume, onToggle, onChange}) {
    return (
        <div className={styles.control}>
            <button className={styles.toggle} onClick= {onToggle} aria-pressed={muted} aria-label={muted ? "Unmute" : "Mute"}>
                <img src={muted ? soundOff : soundOn} alt="" />
            </button>
            <input 
                className={styles.slider} 
                type="range" 
                min="0" 
                max="100" 
                value={volume} 
                onChange={(e) => setVolumentPct(e.target.value)}
                aria-label="Volume"
            />
        </div>
    );
}