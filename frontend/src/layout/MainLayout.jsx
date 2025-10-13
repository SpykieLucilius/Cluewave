import SoundControl from '../components/SoundControl/SoundControl.jsx';
import { Outlet } from 'react-router-dom';

export default function MainLayout() {
    return (
        <>
            <SoundControl />
            <Outlet />
        </>
    );
}