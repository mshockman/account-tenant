import './App.css'
import {Link, Route, Routes} from "react-router";
import Dashboard from "./pages/Dashboard.tsx";
import {AppBar, Container, Toolbar, Typography} from "@mui/material";
import {NotificationProvider} from "./shared/components/notifications.tsx";
import RealmPage from "./realms/RealmPage.tsx";
import {TenantPage} from "./tenants/TenantPage.tsx";

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
                      Tenant Service Admin Panel
                  </Typography>
              </Toolbar>
          </AppBar>
          <Container maxWidth="lg" sx={{marginTop: 4}}>
              <NotificationProvider>
                  <Routes>
                      <Route path="/" index={true} element={<Dashboard/>} />
                      <Route path="/realms/:id" element={<RealmPage />} />
                      <Route path="/realms/tenants/:id" element={<TenantPage />} />
                  </Routes>
              </NotificationProvider>
          </Container>
      </>
  )
}

export default App
