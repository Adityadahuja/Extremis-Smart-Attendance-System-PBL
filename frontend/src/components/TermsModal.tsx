import React from 'react';
import { X, ShieldCheck } from 'lucide-react';

interface TermsModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const TermsModal: React.FC<TermsModalProps> = ({ isOpen, onClose }) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-xs">
      <div className="bg-white rounded-2xl max-w-sm w-full p-6 shadow-xl border border-slate-100 flex flex-col max-h-[85vh] animate-in fade-in zoom-in-95 duration-150">
        <div className="flex items-center justify-between pb-3 border-b border-slate-100 mb-3">
          <div className="flex items-center gap-2">
            <ShieldCheck className="w-5 h-5 text-[#1D4ED8]" />
            <h3 className="font-bold text-slate-900 text-base">Terms & Conditions</h3>
          </div>
          <button
            onClick={onClose}
            className="p-1 text-slate-400 hover:text-slate-600 rounded-lg hover:bg-slate-100 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="overflow-y-auto space-y-3 text-xs text-slate-600 leading-relaxed pr-1">
          <p>
            Welcome to <strong>Extremis Student Attendance System</strong>. By using this service, you agree to the following institutional compliance guidelines:
          </p>
          <div className="p-3 bg-blue-50/60 rounded-xl border border-blue-100/60">
            <h4 className="font-bold text-blue-900 mb-1">1. Geolocation Verification</h4>
            <p>
              Attendance can only be marked while physically present inside the designated Arya College campus perimeter. Mock or spoofed GPS locations are strictly logged and flagged.
            </p>
          </div>
          <div className="p-3 bg-slate-50 rounded-xl border border-slate-100">
            <h4 className="font-bold text-slate-800 mb-1">2. Selfie Verification</h4>
            <p>
              A real-time front camera image is captured to prevent proxy check-ins. Images are encrypted and preserved only for institutional audit records.
            </p>
          </div>
          <div className="p-3 bg-slate-50 rounded-xl border border-slate-100">
            <h4 className="font-bold text-slate-800 mb-1">3. Single Device Hardware Binding</h4>
            <p>
              Your student roll number binds permanently to your registered mobile device upon initial sign-in. Secondary logins on other phones or PCs will lock your account until admin reset.
            </p>
          </div>
        </div>

        <div className="pt-4 mt-3 border-t border-slate-100 flex justify-end">
          <button
            type="button"
            onClick={onClose}
            className="w-full py-2.5 bg-[#1D4ED8] hover:bg-[#1e40af] text-white font-semibold rounded-xl text-sm transition-all shadow-sm shadow-blue-500/20"
          >
            I Understand & Agree
          </button>
        </div>
      </div>
    </div>
  );
};
