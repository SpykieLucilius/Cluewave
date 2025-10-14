import SoundControl from '../components/SoundControl/SoundControl.jsx';
import { MusicProvider } from '../context/audio/MusicProvider.jsx';
import { SoundProvider } from '../context/audio/SoundContext.jsx';

export default function MainLayout({ children }) {
    return (
        <SoundProvider>
            <MusicProvider>
                <SoundControl />
                {children}
            </MusicProvider>
        </SoundProvider> 
    );
}