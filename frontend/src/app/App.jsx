// frontend/src/app/App.jsx
import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";

import Home from "../pages/Home/Home.jsx";
import Login from "../pages/Login/Login.jsx";
import Register from "../pages/Register/Register.jsx";
import NotFound from "../pages/NotFound.jsx";

import { AuthProvider } from "../context/auth/AuthContext.jsx";
import { MusicProvider } from "../context/audio/MusicProvider.jsx";
import MainLayout from "../layout/MainLayout.jsx";

import CreateRoom from "../pages/CreateRoom/CreateRoom.jsx";
import JoinRoom from "../pages/JoinRoom/JoinRoom.jsx";

const App = () => {
  return (
    <AuthProvider>
      <MusicProvider>
        <BrowserRouter>
          <MainLayout>
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />

              {/* nouvelles pages jeu */}
              <Route path="/create" element={<CreateRoom />} />
              <Route path="/join" element={<JoinRoom />} />

              <Route path="*" element={<NotFound />} />
            </Routes>
          </MainLayout>
        </BrowserRouter>
      </MusicProvider>
    </AuthProvider>
  );
};

export default App;
