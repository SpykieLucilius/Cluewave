import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext.jsx';
import '../../styles/Home.css';

/**
 * Home page displayed to all users.  Shows navigation options for playing
 * games and a simple footer.  When a user is authenticated their
 * username is displayed along with a logout button.  Unauthenticated
 * visitors are invited to login or register.
 */
export default function Home() {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  const handlePlay = () => {
    // In the future this will navigate to game creation/join flows.  For now,
    // redirect unauthenticated users to login.
    if (!user) {
      navigate('/login');
    } else {
      // TODO: implement game flow; for now show alert
      alert('Game creation not yet implemented');
    }
  };

  return (
    <main>
      <div className="home-container">
        <h1 className="home-title">Cluewave</h1>
        <div className="home-buttons">
          <button className="home-button" onClick={handlePlay}>
            Play with friends
          </button>
          <button className="home-button" onClick={handlePlay}>
            Join a game
          </button>
        </div>
        {/* Authentication status */}
        <div style={{ marginTop: '20px' }}>
          {user ? (
            <>
              <p style={{ color: '#ffffff' }}>Welcome, {user.username}!</p>
              <button className="home-button" onClick={logout}>Logout</button>
            </>
          ) : (
            <>
              <button className="home-button" onClick={() => navigate('/login')}>
                Login
              </button>
              <button className="home-button" onClick={() => navigate('/register')}>
                Register
              </button>
            </>
          )}
        </div>
      </div>
      <footer className="home-footer">
        <p>
          2025 Cluewave. Open-source online party game project. Inspired by
          Wavelenght. Website under construction.
        </p>
      </footer>
    </main>
  );
}
