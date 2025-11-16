import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';

import Home from '../pages/Home/Home.jsx';
import Login from '../pages/Login/Login.jsx';
import Register from '../pages/Register/Register.jsx';
import Profile from '../pages/Profile/Profile.jsx';
import NotFound from '../pages/NotFound.jsx';

import { AuthProvider } from '../context/auth/AuthContext.jsx';
import MainLayout from '../layout/MainLayout.jsx';
import CreateRoom from '../pages/CreateRoom/CreateRoom.jsx';
import JoinRoom from '../pages/JoinRoom/JoinRoom.jsx';

/**
 * Top‑level application component.  Wraps all routes in the AuthProvider so
 * that authentication state is available throughout the app.  Routes are
 * nested inside the MainLayout which provides common UI (e.g. navbar).
 */
export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <MainLayout>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/profile" element={<Profile />} />
            <Route path="/create" element={<CreateRoom />} />
            <Route path="/join" element={<JoinRoom />} />
            <Route path="*" element={<NotFound />} />
          </Routes>
        </MainLayout>
      </BrowserRouter>
    </AuthProvider>
  );
}