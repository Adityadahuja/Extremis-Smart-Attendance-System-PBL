import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Layout } from './components/Layout';
import { LoginPage } from './pages/LoginPage';
import { PermissionsPage } from './pages/PermissionsPage';
import { DashboardPage } from './pages/DashboardPage';
import { NotificationsPage } from './pages/NotificationsPage';
import { AttendanceReportPage } from './pages/AttendanceReportPage';
import { CheckInProcessPage } from './pages/CheckInProcessPage';
import { ProfilePage } from './pages/ProfilePage';
import { authService } from './services/api';

// Route guards
const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const user = authService.getCurrentUser();
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  if (!user.hasAcceptedPermissions) {
    return <Navigate to="/permissions" replace />;
  }
  return <>{children}</>;
};

export const App: React.FC = () => {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public Auth Routes */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/permissions" element={<PermissionsPage />} />

        {/* Main Student Shell inside Layout */}
        <Route
          element={
            <ProtectedRoute>
              <Layout />
            </ProtectedRoute>
          }
        >
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/notifications" element={<NotificationsPage />} />
          <Route path="/report" element={<AttendanceReportPage />} />
          <Route path="/check-in" element={<CheckInProcessPage />} />
          <Route path="/check-out" element={<CheckInProcessPage />} />
          <Route path="/profile" element={<ProfilePage />} />
        </Route>

        {/* Default Fallback */}
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </BrowserRouter>
  );
};

export default App;
