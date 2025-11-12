import NavBar from '../components/NavBar/NavBar.jsx';
import { MusicProvider } from '../context/audio/MusicProvider.jsx';
import { SoundProvider } from '../context/audio/SoundContext.jsx';

export default function MainLayout({ children }) {
    return (
        <SoundProvider>
        <MusicProvider>
            <NavBar />
                {children}
            </MusicProvider>
        </SoundProvider>
    );
}