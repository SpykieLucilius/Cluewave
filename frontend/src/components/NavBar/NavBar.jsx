import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/auth/AuthContext.jsx';
import SoundControl from '../SoundControl/SoundControl.jsx';
import styles from './NavBar.module.css';

export default function NavBar() {
  const navigate = useNavigate();
  const { user, logout, updateProfile } = useAuth();
  const [open, setOpen] = useState(false);
  const [editMode, setEditMode] = useState(false);
  const [formData, setFormData] = useState({ username: user?.username || '', email: user?.email || '' });
  const cardRef = useRef(null);

  useEffect(() => {
    const handleOutsideClick = (e) => {
      if (cardRef.current && !cardRef.current.contains(e.target)) {
        setOpen(false);
        setEditMode(false);
      }
    };
    const handleKey = (e) => {
      if (e.key === 'Escape') {
        setOpen(false);
        setEditMode(false);
      }
    };
    document.addEventListener('mousedown', handleOutsideClick);
    document.addEventListener('keydown', handleKey);
    return () => {
      document.removeEventListener('mousedown', handleOutsideClick);
      document.removeEventListener('keydown', handleKey);
    };
  }, []);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSave = async () => {
    await updateProfile(formData);
    setEditMode(false);
    setOpen(false);
  };

  return (
    <div className={styles.floatingBar}>
      {!user ? (
        <button className={styles.iconButton} onClick={() => navigate('/login')}>Login</button>
      ) : (
        <div className={styles.profileWrap}>
          <button
            className={styles.iconButton}
            onClick={() => setOpen((v) => !v)}
            aria-haspopup="dialog"
            aria-expanded={open}
          >
            Profile
          </button>
          {open && (
            <div ref={cardRef} role="dialog" className={styles.profileCard}>
              {!editMode ? (
                <>
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
                    <button className={styles.logoutBtn} onClick={logout}>Logout</button>
                    <button className={styles.editBtn} onClick={() => { setEditMode(true); setFormData({ username: user.username, email: user.email }); }}>Edit</button>
                  </div>
                </>
              ) : (
                <>
                  <div className={styles.cardHeader}>Edit profile</div>
                  <input
                    className={styles.inputField}
                    name="username"
                    value={formData.username}
                    onChange={handleChange}
                    placeholder="Username"
                  />
                    <input
                    className={styles.inputField}
                    name="email"
                    value={formData.email}
                    onChange={handleChange}
                    placeholder="Email"
                  />
                  <div className={styles.cardActions}>
                    <button className={styles.saveBtn} onClick={handleSave}>Save</button>
                    <button className={styles.cancelBtn} onClick={() => setEditMode(false)}>Cancel</button>
                  </div>
                </>
              )}
            </div>
          )}
        </div>
      )}
      <SoundControl />
    </div>
  );
}
