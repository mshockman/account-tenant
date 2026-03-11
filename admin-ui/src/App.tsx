import './App.css'
import {Link, Route, Routes} from "react-router";
import Dashboard from "./pages/Dashboard.tsx";
import {AppBar, Container, Toolbar, Typography} from "@mui/material";
import RealmPage from "./pages/RealmPage.tsx";

function App() {
  return (
      <>
          <AppBar position="static">
              <Toolbar>
                  <Typography
                      variant="h6"
                      component={Link}
                      to="/"
                      sx={{
                          color: 'inherit',
                          textDecoration: 'none',
                          cursor: 'pointer',
                      }}
                  >
                      Admin
                  </Typography>
              </Toolbar>
          </AppBar>
          <Container maxWidth="lg" sx={{marginTop: 4}}>
              <Routes>
                  <Route path="/" index={true} element={<Dashboard/>} />
                  <Route path="/realms/:id" element={<RealmPage />} />
              </Routes>
          </Container>
      </>
  )
}

export default App
