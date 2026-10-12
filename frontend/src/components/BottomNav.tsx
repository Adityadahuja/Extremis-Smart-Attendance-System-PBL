import React from 'react';
import { NavLink } from 'react-router-dom';
import { Home, FileText, User } from 'lucide-react';

export const BottomNav: React.FC = () => {
  return (
    <nav className="fixed bottom-0 left-0 right-0 w-full md:hidden bg-white border-t border-slate-200/80 h-16 flex items-center justify-around z-40 px-2 shadow-[0_-4px_20px_rgba(0,0,0,0.03)]">
      <NavLink
        to="/dashboard"
        className={({ isActive }) =>
          `flex flex-col items-center justify-center gap-1 min-w-[70px] py-1 transition-colors ${
            isActive ? 'text-[#1D4ED8] font-bold' : 'text-slate-400 hover:text-slate-600 font-medium'
          }`
        }
      >
        {({ isActive }) => (
          <>
            <Home className={`w-5 h-5 ${isActive ? 'stroke-[2.5px]' : 'stroke-2'}`} />
            <span className="text-[11px]">Home</span>
          </>
        )}
      </NavLink>

      <NavLink
        to="/report"
        className={({ isActive }) =>
          `flex flex-col items-center justify-center gap-1 min-w-[70px] py-1 transition-colors ${
            isActive ? 'text-[#1D4ED8] font-bold' : 'text-slate-400 hover:text-slate-600 font-medium'
          }`
        }
      >
        {({ isActive }) => (
          <>
            <FileText className={`w-5 h-5 ${isActive ? 'stroke-[2.5px]' : 'stroke-2'}`} />
            <span className="text-[11px]">Report</span>
          </>
        )}
      </NavLink>

      <NavLink
        to="/profile"
        className={({ isActive }) =>
          `flex flex-col items-center justify-center gap-1 min-w-[70px] py-1 transition-colors ${
            isActive ? 'text-[#1D4ED8] font-bold' : 'text-slate-400 hover:text-slate-600 font-medium'
          }`
        }
      >
        {({ isActive }) => (
          <>
            <User className={`w-5 h-5 ${isActive ? 'stroke-[2.5px]' : 'stroke-2'}`} />
            <span className="text-[11px]">Profile</span>
          </>
        )}
      </NavLink>
    </nav>
  );
};
