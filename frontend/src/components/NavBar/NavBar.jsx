import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/auth/AuthContext.jsx';
import SoundControl from '../SoundControl/SoundControl.jsx';
import styles from './NavBar.module.css';

export default function NavBar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  return (
    <header className={styles.navBar}>
      <div className={styles.left}>
        {!user ? (
          <button className={styles.primary} onClick={() => navigate('/login')}>
            Login
          </button>
        ) : (
          <div className={styles.userMenu}>
            <button
              className={styles.profile}
              onClick={() => navigate('/profile')}
              title="Voir mon profil"
            >
              {user.username}
            </button>
            <button className={styles.ghost} onClick={logout}>Logout</button>
          </div>
        )}
      </div>
      <div className={styles.right}>
        <SoundControl />
      </div>
    </header>
  );
}
