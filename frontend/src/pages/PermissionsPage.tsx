import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { MapPin, Camera, Check, AlertCircle, Settings, ArrowRight, ArrowLeft } from 'lucide-react';
import { authService } from '../services/api';
import { TermsModal } from '../components/TermsModal';

export const PermissionsPage: React.FC = () => {
  const navigate = useNavigate();
  const [locationGranted, setLocationGranted] = useState(false);
  const [cameraGranted, setCameraGranted] = useState(false);
  const [termsAgreed, setTermsAgreed] = useState(false);
  const [showTermsModal, setShowTermsModal] = useState(false);
  const [showSettingsModal, setShowSettingsModal] = useState(false);

  // Check initial permissions
  useEffect(() => {
    if (navigator.permissions) {
      navigator.permissions.query({ name: 'geolocation' }).then((status) => {
        if (status.state === 'granted') setLocationGranted(true);
      }).catch(() => {});

      navigator.permissions.query({ name: 'camera' as PermissionName }).then((status) => {
        if (status.state === 'granted') setCameraGranted(true);
      }).catch(() => {});
    }
  }, []);

  const requestLocation = () => {
    if (!navigator.geolocation) {
      alert('Geolocation is not supported by your browser.');
      return;
    }
    navigator.geolocation.getCurrentPosition(
      () => setLocationGranted(true),
      (err) => {
        console.warn('Location permission error:', err);
        setLocationGranted(false);
      },
      { enableHighAccuracy: true, timeout: 10000 }
    );
  };

  const requestCamera = async () => {
    if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
      alert('Camera access is not supported or requires localhost/HTTPS.');
      return;
    }
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ video: true });
      stream.getTracks().forEach((track) => track.stop());
      setCameraGranted(true);
    } catch (err) {
      console.warn('Camera permission error:', err);
      setCameraGranted(false);
    }
  };

  const handleContinue = () => {
    if (locationGranted && cameraGranted && termsAgreed) {
      authService.updatePermissions(true);
      navigate('/dashboard');
    }
  };

  const canContinue = locationGranted && cameraGranted && termsAgreed;

  return (
    <div className="flex-1 flex flex-col px-5 py-4">
      {/* Header */}
      <div className="flex items-center gap-3 py-2 mb-2">
        <button
          onClick={() => navigate('/login')}
          className="p-1 -ml-1 text-slate-500 hover:text-slate-800 transition-colors"
        >
          <ArrowLeft className="w-5 h-5" />
        </button>
        <h1 className="text-lg font-extrabold text-slate-900 tracking-tight">Required Permissions</h1>
      </div>

      <p className="text-xs text-slate-500 leading-relaxed mb-5">
        To mark your attendance, please allow the following permissions.
      </p>

      {/* Permission Cards */}
      <div className="space-y-3.5 mb-6">
        {/* Card 1: Access Location */}
        <div
          onClick={requestLocation}
          className="bg-white rounded-2xl p-4 border border-slate-100 shadow-sm flex items-center justify-between cursor-pointer active:scale-[0.99] transition-all hover:border-blue-100"
        >
          <div className="flex items-center gap-3.5">
            <div className="w-11 h-11 rounded-xl bg-blue-50 text-[#1D4ED8] flex items-center justify-center shrink-0">
              <MapPin className="w-5 h-5 stroke-[2.2px]" />
            </div>
            <div>
              <div className="font-bold text-slate-900 text-sm">Access Location</div>
              <div className={`text-[11px] font-semibold mt-0.5 ${locationGranted ? 'text-emerald-600' : 'text-red-500'}`}>
                {locationGranted ? 'Granted' : 'Not Granted'}
              </div>
            </div>
          </div>

          <div
            className={`w-6 h-6 rounded-full flex items-center justify-center text-white shrink-0 shadow-xs ${
              locationGranted ? 'bg-emerald-500' : 'bg-red-500'
            }`}
          >
            {locationGranted ? <Check className="w-3.5 h-3.5 stroke-[3px]" /> : <span className="font-black text-xs">!</span>}
          </div>
        </div>

        {/* Card 2: Camera Access */}
        <div
          onClick={requestCamera}
          className="bg-white rounded-2xl p-4 border border-slate-100 shadow-sm flex items-center justify-between cursor-pointer active:scale-[0.99] transition-all hover:border-blue-100"
        >
          <div className="flex items-center gap-3.5">
            <div className="w-11 h-11 rounded-xl bg-blue-50 text-[#1D4ED8] flex items-center justify-center shrink-0">
              <Camera className="w-5 h-5 stroke-[2.2px]" />
            </div>
            <div>
              <div className="font-bold text-slate-900 text-sm">Camera Access</div>
              <div className={`text-[11px] font-semibold mt-0.5 ${cameraGranted ? 'text-emerald-600' : 'text-red-500'}`}>
                {cameraGranted ? 'Granted' : 'Not Granted'}
              </div>
            </div>
          </div>

          <div
            className={`w-6 h-6 rounded-full flex items-center justify-center text-white shrink-0 shadow-xs ${
              cameraGranted ? 'bg-emerald-500' : 'bg-red-500'
            }`}
          >
            {cameraGranted ? <Check className="w-3.5 h-3.5 stroke-[3px]" /> : <span className="font-black text-xs">!</span>}
          </div>
        </div>
      </div>

      {/* Checkbox: Terms & Conditions */}
      <div className="flex items-center gap-2.5 px-1 mb-8">
        <input
          id="termsCheck"
          type="checkbox"
          checked={termsAgreed}
          onChange={(e) => setTermsAgreed(e.target.checked)}
          className="w-4 h-4 rounded text-[#1D4ED8] focus:ring-blue-500 border-slate-300 cursor-pointer accent-[#1D4ED8]"
        />
        <label htmlFor="termsCheck" className="text-xs text-slate-600 select-none cursor-pointer">
          I agree to Extremis{' '}
          <button
            type="button"
            onClick={(e) => {
              e.preventDefault();
              setShowTermsModal(true);
            }}
            className="text-[#1D4ED8] font-semibold underline hover:text-blue-800"
          >
            Terms & Conditions
          </button>
        </label>
      </div>

      {/* Action Buttons: Settings (Outline) & Continue (Red Gradient) */}
      <div className="flex gap-2.5 mt-auto pb-4">
        <button
          type="button"
          onClick={() => setShowSettingsModal(true)}
          className="h-12 px-5 rounded-xl border-2 border-[#1D4ED8] text-[#1D4ED8] bg-white hover:bg-blue-50 font-bold text-sm flex items-center justify-center gap-2 transition-all shrink-0 cursor-pointer"
        >
          <Settings className="w-4 h-4" />
          <span>Settings</span>
        </button>

        <button
          type="button"
          onClick={handleContinue}
          disabled={!canContinue}
          className={`flex-1 h-12 rounded-xl text-white font-bold text-sm flex items-center justify-center gap-2 transition-all shadow-md ${
            canContinue
              ? 'bg-gradient-to-r from-[#E11D48] to-[#BE123C] hover:from-rose-600 hover:to-rose-700 shadow-rose-500/25 active:scale-[0.98] cursor-pointer'
              : 'bg-slate-300 cursor-not-allowed shadow-none'
          }`}
        >
          <span>Continue</span>
          <ArrowRight className="w-4 h-4 stroke-[2.5px]" />
        </button>
      </div>

      {/* Terms Modal */}
      <TermsModal isOpen={showTermsModal} onClose={() => setShowTermsModal(false)} />

      {/* Settings Instructions Modal */}
      {showSettingsModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/40 backdrop-blur-xs">
          <div className="bg-white rounded-2xl max-w-sm w-full p-5 shadow-xl border border-slate-100">
            <h3 className="font-bold text-slate-900 text-sm mb-2 flex items-center gap-2">
              <AlertCircle className="w-4 h-4 text-blue-600" />
              <span>How to Enable Permissions</span>
            </h3>
            <p className="text-xs text-slate-600 leading-relaxed mb-3">
              If permissions are blocked in your browser:
            </p>
            <ol className="list-decimal list-inside text-xs text-slate-600 space-y-1 mb-4">
              <li>Tap the lock icon 🔒 next to the website URL.</li>
              <li>Select <strong>Site Settings</strong> or <strong>Permissions</strong>.</li>
              <li>Set <strong>Location</strong> and <strong>Camera</strong> to <strong>Allow</strong>.</li>
              <li>Refresh the page and tap the cards again.</li>
            </ol>
            <button
              onClick={() => setShowSettingsModal(false)}
              className="w-full py-2 bg-slate-100 hover:bg-slate-200 text-slate-800 font-bold rounded-xl text-xs transition-colors"
            >
              Close Instructions
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
