import '../../styles/Home.css'

export default function Home() {
  return (
    <main>
        <div className="home-container">
            <h1 className="home-title">Cluewave</h1>
                <div className="home-buttons">
                <button className="home-button">Play with friends</button>
                <button className="home-button">Join a game</button>
            </div>
        </div>
        <footer className="home-footer">
            <p>2025 Cluewave. Open-source online party game project. Inspired by Wavelenght. Website under construction.</p>
        </footer>
    </main>
  )
}