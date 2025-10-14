import styles from './SoundControl.module.css';
import soundOn from './icons/sound-on.svg';
import soundOff from './icons/sound-off.svg';
import { useSound } from '../../context/audio/SoundContext.jsx';

export default function SoundControl() {
  const { muted, volume, toggleMute, setVolumePct } = useSound();

  return (
    <div className={styles.control}>
      <button
        className={styles.toggle}
        onClick={toggleMute}
        aria-pressed={muted}
        aria-label={muted ? "Unmute" : "Mute"}
      >
        <img src={muted ? soundOff : soundOn} alt="" />
      </button>

      <div className={styles.sliderWrapper}>
        <div className={styles.vTrack} style={{ '--v': volume }}>
          <input
            className={styles.slider}
            type="range"
            min="0"
            max="100"
            value={Number(volume)}
            onChange={e => setVolumePct(e.target.value)}
          />
        </div>
      </div>
    </div>
  );
}