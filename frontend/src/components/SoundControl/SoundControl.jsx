export default function SoundControl({ muted, volume, onToggle, onChange}) {
    return (
        <div className={style.control}>
            <button className={styles.toggle} onClick= {onToggle} aria-pressed={muted}/>
            <input className={styles.slider} type="range" min="0" max="100" value={volume} onChange={onChange}/>
        </div>
    );
}