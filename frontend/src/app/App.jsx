import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { AuthProvider } from '../context/auth/AuthContext.jsx';
import Home from '../pages/Home/Home.jsx';
import Login from '../pages/Login/Login.jsx';
import Register from '../pages/Register/Register.jsx';
import NotFound from '../pages/NotFound.jsx';

/**
 * Root application component.  Sets up the router and authentication
 * provider.  Routes are defined for the home page, login, registration
 * and a catch‑all NotFound page.  Additional routes can be added as
 * the application grows.
 */
export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="*" element={<NotFound />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
