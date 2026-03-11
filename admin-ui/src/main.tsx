import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from "react-router"
import './index.css'
import App from './App.tsx'
import {QueryClient, QueryClientProvider} from '@tanstack/react-query'
import { ThemeProvider, createTheme, CssBaseline } from "@mui/material";


const queryClient = new QueryClient()

const theme = createTheme({
    palette: {
        mode: 'dark',
    },
})


createRoot(document.getElementById('root')!).render(
  <StrictMode>
      <ThemeProvider theme={theme}>
          <CssBaseline />
          <QueryClientProvider client={queryClient}>
              <BrowserRouter basename="/admin">
                  <App />
              </BrowserRouter>
          </QueryClientProvider>
      </ThemeProvider>
  </StrictMode>,
)
