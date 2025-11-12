import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/auth/AuthContext.jsx';
import SoundControl from '../SoundControl/SoundControl.jsx';
import styles from './NavBar.module.css';

export default function NavBar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const cardRef = useRef(null);

  // Fermer le menu profil si clic en dehors ou press Escape
  useEffect(() => {
    const onDocClick = (e) => {
      if (cardRef.current && !cardRef.current.contains(e.target)) setOpen(false);
    };
    const onKey = (e) => e.key === 'Escape' && setOpen(false);
    document.addEventListener('mousedown', onDocClick);
    document.addEventListener('keydown', onKey);
    return () => {
      document.removeEventListener('mousedown', onDocClick);
      document.removeEventListener('keydown', onKey);
    };
  }, []);

  return (
    // Barre invisible : on ne montre que les boutons flottants
    <div className={styles.floatingBar} aria-hidden="false">
      {/* À GAUCHE (dans le groupe flottant) */}
      {!user ? (
        <button
          className={styles.iconButton}
          onClick={() => navigate('/login')}
          aria-label="Login"
        >
          Login
        </button>
      ) : (
        <div className={styles.profileWrap}>
          <button
            className={styles.iconButton}
            onClick={() => setOpen((v) => !v)}
            aria-haspopup="dialog"
            aria-expanded={open}
            aria-label="Profile"
          >
            Profile
          </button>

          {open && (
            <div ref={cardRef} role="dialog" className={styles.profileCard}>
              <div className={styles.cardHeader}>Player</div>
              <div className={styles.cardRow}>
                <span className={styles.label}>Name</span>
                <span className={styles.value}>{user.username}</span>
              </div>
              {user.email && (
                <div className={styles.cardRow}>
                  <span className={styles.label}>Email</span>
                  <span className={styles.value}>{user.email}</span>
                </div>
              )}
              <div className={styles.cardActions}>
                <button className={styles.logoutBtn} onClick={logout}>
                  Logout
                </button>
              </div>
            </div>
          )}
        </div>
      )}

      {/* À DROITE : contrôle du son */}
      <SoundControl />
    </div>
  );
}
