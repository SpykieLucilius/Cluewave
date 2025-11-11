import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './styles/index.css'
import MainLayout from './layout/MainLayout.jsx'
import App from './app/App.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <MainLayout>
      <App />
    </MainLayout>
  </StrictMode>
)
