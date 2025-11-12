import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/auth/AuthContext.jsx';
import '../../styles/Home.css';

/**
 * Accueil centré et responsive.
 * - Deux CTA : "Play with friends" / "Join a game"
 * - Si non connecté et on clique => redirection vers /login
 * - Aucun bouton Login/Logout ici (tout passe par la NavBar + card Profile)
 */
export default function Home() {
  const navigate = useNavigate();
  const { user } = useAuth();

  const handlePlay = () => {
    if (!user) {
      navigate('/login');
    } else {
      // TODO: implémenter le flow de création/join de partie
      alert('Game creation not yet implemented');
    }
  };

  return (
    <main>
      <div className="home-container">
        <div className="home-center">
          <h1 className="home-title">Cluewave</h1>

          <div className="home-buttons">
            <button className="home-button" onClick={handlePlay}>
              Play with friends
            </button>
            <button className="home-button" onClick={handlePlay}>
              Join a game
            </button>
          </div>
        </div>
      </div>

      <footer className="home-footer">
        <p>
          2025 Cluewave. Open-source online party game project. Inspired by Wavelenght.
          Website under construction.
        </p>
      </footer>
    </main>
  );
}
