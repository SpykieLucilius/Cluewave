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
  const [formData, setFormData] = useState({
    username: user?.username || '',
    email: user?.email || '',
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  });
  const [error, setError] = useState('');
  const cardRef = useRef(null);

  // Ferme la carte en cas de clic en dehors ou touche Échap
  useEffect(() => {
    const handleOutsideClick = (e) => {
      if (cardRef.current && !cardRef.current.contains(e.target)) {
        setOpen(false);
        setEditMode(false);
        setError('');
      }
    };
    const handleKey = (e) => {
      if (e.key === 'Escape') {
        setOpen(false);
        setEditMode(false);
        setError('');
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
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const resetForm = () => {
    setFormData({
      username: user?.username || '',
      email: user?.email || '',
      currentPassword: '',
      newPassword: '',
      confirmPassword: '',
    });
    setError('');
  };

  const handleSave = async () => {
    // Validation côté client
    if (!formData.currentPassword) {
      setError('Veuillez saisir votre mot de passe actuel.');
      return;
    }
    if (formData.newPassword) {
      if (formData.newPassword.length < 6) {
        setError('Le nouveau mot de passe doit faire au moins 6 caractères.');
        return;
      }
      if (formData.newPassword !== formData.confirmPassword) {
        setError('Le mot de passe de confirmation ne correspond pas.');
        return;
      }
    }
    if (formData.email && !/^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/.test(formData.email)) {
      setError('Format de courriel invalide.');
      return;
    }
    try {
      await updateProfile({
        currentPassword: formData.currentPassword,
        username: formData.username === user?.username ? null : formData.username,
        email: formData.email === user?.email ? null : formData.email,
        newPassword: formData.newPassword || null,
      });
      setEditMode(false);
      setOpen(false);
      setError('');
      resetForm();
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className={styles.floatingBar}>
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
            onClick={() => {
              setOpen(!open);
              setEditMode(false);
              setError('');
              resetForm();
            }}
            aria-haspopup="dialog"
            aria-expanded={open}
            aria-label="Profile"
          >
            Profile
          </button>
          {open && (
            <div ref={cardRef} role="dialog" className={styles.profileCard}>
              {!editMode ? (
                <>
                  <div className={styles.cardHeader}>Profil</div>
                  <div className={styles.cardRow}>
                    <span className={styles.label}>Nom :</span>
                    <span className={styles.value}>{user.username}</span>
                  </div>
                  {user.email && (
                    <div className={styles.cardRow}>
                      <span className={styles.label}>Email :</span>
                      <span className={styles.value}>{user.email}</span>
                    </div>
                  )}
                  <div className={styles.cardActions}>
                    <button
                      className={styles.logoutBtn}
                      onClick={() => {
                        logout();
                        setOpen(false);
                      }}
                    >
                      Logout
                    </button>
                    <button
                      className={styles.editBtn}
                      onClick={() => {
                        setEditMode(true);
                        setError('');
                      }}
                    >
                      Edit
                    </button>
                  </div>
                </>
              ) : (
                <>
                  <div className={styles.cardHeader}>Modifier le profil</div>
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
                  <input
                    className={styles.inputField}
                    type="password"
                    name="currentPassword"
                    value={formData.currentPassword}
                    onChange={handleChange}
                    placeholder="Current password"
                  />
                  <input
                    className={styles.inputField}
                    type="password"
                    name="newPassword"
                    value={formData.newPassword}
                    onChange={handleChange}
                    placeholder="New password"
                  />
                  <input
                    className={styles.inputField}
                    type="password"
                    name="confirmPassword"
                    value={formData.confirmPassword}
                    onChange={handleChange}
                    placeholder="Confirm new password"
                  />
                  {error && <p className={styles.errorText}>{error}</p>}
                  <div className={styles.cardActions}>
                    <button className={styles.saveBtn} onClick={handleSave}>
                      Save
                    </button>
                    <button
                      className={styles.cancelBtn}
                      onClick={() => {
                        setEditMode(false);
                        setError('');
                        resetForm();
                      }}
                    >
                      Cancel
                    </button>
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
