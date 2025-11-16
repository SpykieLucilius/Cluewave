import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';

// Pages
import Home from '../pages/Home/Home.jsx';
import CreateRoom from '../pages/CreateRoom/CreateRoom.jsx';
import JoinRoom from '../pages/JoinRoom/JoinRoom.jsx';
import NotFound from '../pages/NotFound.jsx';

/**
 * Application root defining client side routes.  Adds routes for creating
 * and joining rooms in addition to the existing Home and NotFound pages.
 */
const App = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/create" element={<CreateRoom />} />
        <Route path="/join" element={<JoinRoom />} />
        {/* catch-all route */}
        <Route path="*" element={<NotFound />} />
      </Routes>
    </BrowserRouter>
  );
};

export default App;