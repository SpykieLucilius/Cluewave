import NavBar from '../components/NavBar/NavBar.jsx';

export default function MainLayout({ children }) {
    return (
        <>
            <NavBar />
            {children}
        </>
    );
}
