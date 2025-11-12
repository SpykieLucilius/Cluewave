import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/auth/AuthContext.jsx';
import '../../styles/Home.css';

export default function Home() {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  const handlePlay = () => {
    if (!user) navigate('/login');
    else alert('Game creation not yet implemented');
  };

  return (
    <main>
      <div className="home-container">
        <h1 className="home-title">Cluewave</h1>

        <div className="home-buttons">
          <button className="home-button" onClick={handlePlay}>Play with friends</button>
          <button className="home-button" onClick={handlePlay}>Join a game</button>
        </div>

        <div style={{ marginTop: '20px' }}>
          {user ? (
            <>
              <p style={{ color: '#ffffff' }}>Welcome, {user.username}!</p>
              <button className="home-button" onClick={logout}>Logout</button>
            </>
          ) : null}
        </div>
      </div>

      <footer className="home-footer">
        <p>2025 Cluewave. Open-source online party game project. Inspired by Wavelenght. Website under construction.</p>
      </footer>
    </main>
  );
}
