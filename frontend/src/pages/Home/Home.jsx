import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/auth/AuthContext.jsx';
import '../../styles/Home.css';

/**
 * Home page displayed to visitors.  Provides links to create or join a game
 * depending on authentication status.  Unauthenticated users are prompted to
 * login or register before starting a game.
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
    <div className="home-container">
      <h1>Cluewave</h1>
      <p>Play with friends</p>
      <button onClick={handleCreate}>Play with a Friend</button>
      <button onClick={handleJoin}>Join a Game</button>
      {/* Authentication status */}
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
      <footer>
        <p>2025 Cluewave. Open‑source online party game project.</p>
      </footer>
    </div>
  );
}