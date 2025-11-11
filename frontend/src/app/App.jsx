import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";

import Home from "../pages/Home/Home.jsx";
import Login from "../pages/Login/Login.jsx";
import Register from "../pages/Register/Register.jsx";
import NotFound from "../pages/NotFound.jsx";

import { AuthProvider } from "../context/auth/AuthContext.jsx";
import { MusicProvider } from "../context/audio/MusicProvider.jsx";
import MainLayout from "../layout/MainLayout.jsx";

const App = () => {
  return (
    <BrowserRouter>
      <AuthProvider>
        <MusicProvider>
          <MainLayout>
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route path="*" element={<NotFound />} />
            </Routes>
          </MainLayout>
        </MusicProvider>
      </AuthProvider>
    </BrowserRouter>
  );
};

export default App;
