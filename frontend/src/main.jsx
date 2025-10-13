import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './styles/index.css'
import MainLayout from './layout/MainLayout.jsx'
import Home from './pages/Home/Home.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <MainLayout>
      <Home />
    </MainLayout>
  </StrictMode>
)
