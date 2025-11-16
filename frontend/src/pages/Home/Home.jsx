import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/auth/AuthContext.jsx';
import '../../styles/Home.css';

/**
 * Landing page displayed to visitors and logged‑in users.  Presents a
 * friendly hero section with calls to action for creating or joining a
 * game and displays basic authentication controls.  The design aims to
 * minimize friction by showing only two primary actions and making them
 * prominent.  A dark global background with a light card is used to
 * integrate with the site’s overall theme.
 */
export default function Home() {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  const handleCreate = () => {
    if (!user) {
      navigate('/login');
    } else {
      navigate('/create');
    }
  };

  const handleJoin = () => {
    navigate('/join');
  };

  return (
    <div className="home-wrapper">
      <header className="hero">
        <h1>Cluewave</h1>
        <p>An online guessing game for you and your best friend.</p>
        <div className="cta-buttons">
          <button onClick={handleCreate}>Create Room</button>
          <button onClick={handleJoin}>Join Room</button>
        </div>
      </header>
      <section className="auth-controls">
        {user ? (
          <>
            <p>Welcome, {user.username}!</p>
            <button onClick={logout}>Logout</button>
          </>
        ) : (
          <>
            <button onClick={() => navigate('/login')}>Login</button>
            <button onClick={() => navigate('/register')}>Register</button>
          </>
        )}
      </section>
      <footer className="home-footer">
         <p>© {new Date().getFullYear()} Cluewave. Inspired by Wavelenght. Open-source party game. Currently in development.</p>
      </footer>
    </div>
  );
}