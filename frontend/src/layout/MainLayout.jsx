import SoundControl from '../components/SoundControl/SoundControl.jsx';
import { SoundProvider } from '../context/SoundContext.jsx';

export default function MainLayout({ children }) {
    return (
        <SoundProvider>
            <SoundControl />
            {children}
        </SoundProvider>
    );
}