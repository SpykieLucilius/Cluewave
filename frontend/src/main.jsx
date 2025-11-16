// frontend/src/main.jsx
import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './app/App.jsx';
import './styles/index.css';

import { SoundProvider } from './context/audio/SoundProvider.jsx';
import { MusicProvider } from './context/audio/MusicProvider.jsx';

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <SoundProvider>
      <MusicProvider>
        <App />
      </MusicProvider>
    </SoundProvider>
  </React.StrictMode>
);
