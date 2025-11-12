import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/auth/AuthContext.jsx';
import SoundControl from '../SoundControl/SoundControl.jsx';
import styles from './NavBar.module.css';

/**
 * Floating navigation bar with login/profile and sound control.
 *
 * - Unauthenticated: shows a “Login” button.
 * - Authenticated: shows a “Profile” button that toggles an overlay card.
 *   • View mode: displays name/email + Logout + Edit buttons.
 *   • Edit mode: allows editing username, email, and password (requires current password).
 */
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

  // Close card on click outside or Escape key.
  useEffect(() => {
    const handleOutside = (e) => {
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
    document.addEventListener('mousedown', handleOutside);
    document.addEventListener('keydown', handleKey);
    return () => {
      document.removeEventListener('mousedown', handleOutside);
      document.removeEventListener('keydown', handleKey);
    };
  }, []);

  // Reset form to current values.
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

  // Generic change handler.
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  // Validate inputs and submit.
  const handleSave = async () => {
    if (!formData.currentPassword) {
      setError('Please enter your current password.');
      return;
    }
    if (formData.newPassword) {
      if (formData.newPassword.length < 6) {
        setError('New password must be at least 6 characters.');
        return;
      }
      if (formData.newPassword !== formData.confirmPassword) {
        setError('New password and confirmation do not match.');
        return;
      }
    }
    if (formData.email) {
      const emailRegex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/;
      if (!emailRegex.test(formData.email)) {
        setError('Invalid email format.');
        return;
      }
    }
    try {
      await updateProfile({
        currentPassword: formData.currentPassword,
        username:
          formData.username === user?.username || formData.username === ''
            ? null
            : formData.username,
        email:
          formData.email === user?.email || formData.email === ''
            ? null
            : formData.email,
        newPassword: formData.newPassword || null,
      });
      setEditMode(false);
      setOpen(false);
      setError('');
      resetForm();
    } catch (err) {
      setError(err.message || 'Failed to update profile.');
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
                  <div className={styles.cardHeader}>Profile</div>
                  <div className={styles.cardRow}>
                    <span className={styles.label}>Name</span>
                    <span className={styles.value}>{user?.username || ''}</span>
                  </div>
                  {user?.email && (
                    <div className={styles.cardRow}>
                      <span className={styles.label}>Email</span>
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
                  <div className={styles.cardHeader}>Edit Profile</div>
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
                      type="email"
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
                    {error && <div className={styles.errorText}>{error}</div>}
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
