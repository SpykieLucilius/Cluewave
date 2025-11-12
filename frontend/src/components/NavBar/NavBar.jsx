import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/auth/AuthContext.jsx';
import SoundControl from '../SoundControl/SoundControl.jsx';
import styles from './NavBar.module.css';

/**
 * Barre de navigation « invisible » : seuls les boutons sont visibles
 * en haut à droite.  Si l'utilisateur n'est pas connecté, on affiche
 * un bouton Login.  Sinon, un bouton Profile qui ouvre une petite
 * carte en surimpression avec les infos et un bouton Logout.
 */
export default function NavBar() {
  const navigate = useNavigate();
  const { user, logout } = useAuth();
  const [open, setOpen] = useState(false);
  const cardRef = useRef(null);

  // Ferme la carte profil en cas de clic extérieur ou de touche Échap
  useEffect(() => {
    const handleOutsideClick = (e) => {
      if (cardRef.current && !cardRef.current.contains(e.target)) {
        setOpen(false);
      }
    };
    const handleKey = (e) => {
      if (e.key === 'Escape') setOpen(false);
    };
    document.addEventListener('mousedown', handleOutsideClick);
    document.addEventListener('keydown', handleKey);
    return () => {
      document.removeEventListener('mousedown', handleOutsideClick);
      document.removeEventListener('keydown', handleKey);
    };
  }, []);

  return (
    <div className={styles.floatingBar}>
      {/* Bouton Login ou Profile */}
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

      {/* Bouton de contrôle du son */}
      <SoundControl />
    </div>
  );
}
