import React, { useState } from 'react';
import { Clock, RotateCcw, ChevronUp, ChevronDown } from 'lucide-react';
import { useAttendanceWindow } from '../hooks/useAttendanceWindow';

export const DevTimeToggle: React.FC = () => {
  const [isOpen, setIsOpen] = useState(false);
  const { currentTimeStr, overrideTime, resetCheckState } = useAttendanceWindow();

  return (
    <div className="fixed top-2 right-2 z-50 flex flex-col items-end">
      {/* Discreet Trigger Button */}
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="flex items-center gap-1.5 px-2.5 py-1 text-[11px] font-semibold bg-white/90 backdrop-blur-xs text-slate-600 rounded-full border border-slate-200/80 shadow-xs hover:bg-slate-50 transition-all"
        title="Developer Time Simulator"
      >
        <Clock className="w-3.5 h-3.5 text-[#1D4ED8]" />
        <span>{currentTimeStr}</span>
        {isOpen ? <ChevronUp className="w-3 h-3 text-slate-400" /> : <ChevronDown className="w-3 h-3 text-slate-400" />}
      </button>

      {/* Expanded Control Box */}
      {isOpen && (
        <div className="mt-1.5 bg-white rounded-2xl p-3 shadow-xl border border-slate-200/80 w-64 text-xs animate-in fade-in slide-in-from-top-2 duration-150">
          <div className="flex items-center justify-between pb-2 mb-2 border-b border-slate-100">
            <span className="font-bold text-slate-800 flex items-center gap-1">
              <span>⏱️</span> Time Simulator
            </span>
            <button
              onClick={resetCheckState}
              className="text-[10px] text-red-600 hover:text-red-700 flex items-center gap-0.5 font-medium"
              title="Clear today's check-in/out records"
            >
              <RotateCcw className="w-2.5 h-2.5" />
              Reset Day
            </button>
          </div>

          <p className="text-[10px] text-slate-400 mb-2">
            Switch simulated time to demo all window states:
          </p>

          <div className="grid grid-cols-2 gap-1.5 mb-2">
            <button
              onClick={() => overrideTime('08:40')}
              className="px-2 py-1.5 bg-slate-100 hover:bg-blue-50 hover:text-blue-700 rounded-lg text-slate-700 font-medium transition-colors text-left"
            >
              <div className="font-bold">08:40 AM</div>
              <div className="text-[9px] text-slate-400">Before Window</div>
            </button>

            <button
              onClick={() => overrideTime('08:55')}
              className="px-2 py-1.5 bg-blue-50 border border-blue-200 hover:bg-blue-100 text-[#1D4ED8] rounded-lg font-bold transition-colors text-left"
            >
              <div>08:55 AM</div>
              <div className="text-[9px] text-blue-500 font-normal">Check In Window</div>
            </button>

            <button
              onClick={() => overrideTime('11:30')}
              className="px-2 py-1.5 bg-slate-100 hover:bg-amber-50 hover:text-amber-700 rounded-lg text-slate-700 font-medium transition-colors text-left"
            >
              <div className="font-bold">11:30 AM</div>
              <div className="text-[9px] text-slate-400">Window Closed</div>
            </button>

            <button
              onClick={() => overrideTime('15:45')}
              className="px-2 py-1.5 bg-red-50 border border-red-200 hover:bg-red-100 text-[#E11D48] rounded-lg font-bold transition-colors text-left"
            >
              <div>03:45 PM</div>
              <div className="text-[9px] text-red-500 font-normal">Check Out Time</div>
            </button>
          </div>

          <button
            onClick={() => overrideTime(null)}
            className="w-full py-1 text-center text-slate-500 hover:text-slate-800 text-[10px] font-medium border-t border-slate-100 pt-1.5 transition-colors"
          >
            Use Real System Time
          </button>
        </div>
      )}
    </div>
  );
};
