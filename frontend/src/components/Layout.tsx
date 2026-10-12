import React from 'react';
import { Outlet, useLocation } from 'react-router-dom';
import { BottomNav } from './BottomNav';
import { DevTimeToggle } from './DevTimeToggle';

export const Layout: React.FC = () => {
  const location = useLocation();

  // Show bottom nav on main tabs
  const showBottomNav = ['/dashboard', '/report', '/profile'].includes(location.pathname);

  return (
    <div className="min-h-screen bg-slate-100 flex justify-center selection:bg-blue-600 selection:text-white">
      {/* Responsive Shell: full-width on mobile, spacious max-w-6xl on desktop/tablet */}
      <div className="w-full max-w-6xl min-h-screen bg-[#F5F9FF] shadow-sm relative flex flex-col overflow-x-hidden md:border-x md:border-slate-200/50">
        {/* Dev Time Toggle */}
        <DevTimeToggle />

        {/* Main Content Area */}
        <main className={`flex-1 flex flex-col ${showBottomNav ? 'pb-20 md:pb-8' : 'pb-6'}`}>
          <Outlet />
        </main>

        {/* Bottom Navigation */}
        {showBottomNav && <BottomNav />}
      </div>
    </div>
  );
};
