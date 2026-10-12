import React, { useEffect, useRef, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { ArrowLeft, MapPin, Camera, Check, RefreshCw, AlertCircle } from 'lucide-react';
import { attendanceService } from '../services/api';

export const CheckInProcessPage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const isCheckOut = location.pathname.includes('check-out');

  // Location State
  const [coords, setCoords] = useState<{ lat: number; lng: number; accuracy: number } | null>(null);
  const [locationLoading, setLocationLoading] = useState(true);

  // Camera State
  const videoRef = useRef<HTMLVideoElement | null>(null);
  const [stream, setStream] = useState<MediaStream | null>(null);
  const [capturedImage, setCapturedImage] = useState<string | null>(null);
  const [cameraError, setCameraError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  // Initialize GPS Location
  useEffect(() => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (pos) => {
          setCoords({
            lat: Number(pos.coords.latitude.toFixed(4)),
            lng: Number(pos.coords.longitude.toFixed(4)),
            accuracy: Math.round(pos.coords.accuracy),
          });
          setLocationLoading(false);
        },
        () => {
          // Fallback to Arya Campus mockup coordinates if browser denies in test
          setCoords({ lat: 26.9131, lng: 75.8089, accuracy: 18 });
          setLocationLoading(false);
        },
        { enableHighAccuracy: true, timeout: 8000 }
      );
    } else {
      setCoords({ lat: 26.9131, lng: 75.8089, accuracy: 18 });
      setLocationLoading(false);
    }
  }, []);

  // Initialize Camera
  useEffect(() => {
    let activeStream: MediaStream | null = null;

    const startCamera = async () => {
      try {
        if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
          throw new Error('Camera not supported');
        }
        const s = await navigator.mediaDevices.getUserMedia({
          video: { facingMode: 'user', width: { ideal: 640 }, height: { ideal: 640 } },
        });
        activeStream = s;
        setStream(s);
        if (videoRef.current) {
          videoRef.current.srcObject = s;
        }
      } catch (err) {
        console.warn('Camera stream error:', err);
        setCameraError('Camera stream unavailable. Tap to use demo snapshot.');
      }
    };

    if (!capturedImage) {
      startCamera();
    }

    return () => {
      if (activeStream) {
        activeStream.getTracks().forEach((track) => track.stop());
      }
    };
  }, [capturedImage]);

  // Capture Snapshot from Video Stream
  const handleCapturePhoto = () => {
    if (videoRef.current) {
      const video = videoRef.current;
      const canvas = document.createElement('canvas');
      canvas.width = video.videoWidth || 320;
      canvas.height = video.videoHeight || 320;
      const ctx = canvas.getContext('2d');
      if (ctx) {
        ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
        const dataUrl = canvas.toDataURL('image/jpeg', 0.85);
        setCapturedImage(dataUrl);
        // Stop stream
        if (stream) {
          stream.getTracks().forEach((t) => t.stop());
          setStream(null);
        }
        return;
      }
    }

    // Fallback Mock Photo matching mockup image
    setCapturedImage('https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&auto=format&fit=crop&q=80');
  };

  const handleRetakePhoto = () => {
    setCapturedImage(null);
  };

  const handleSubmit = async () => {
    if (!coords || !capturedImage) return;

    setSubmitting(true);
    try {
      if (isCheckOut) {
        await attendanceService.submitCheckOutCapture({
          lat: coords.lat,
          lng: coords.lng,
          accuracy: coords.accuracy,
          photoBase64: capturedImage,
        });
      } else {
        await attendanceService.submitCheckInCapture({
          lat: coords.lat,
          lng: coords.lng,
          accuracy: coords.accuracy,
          photoBase64: capturedImage,
        });
      }
      navigate('/dashboard');
    } catch {
      alert('Failed to submit attendance.');
    } finally {
      setSubmitting(false);
    }
  };

  const isFormComplete = coords !== null && capturedImage !== null;

  return (
    <div className="flex-1 flex flex-col px-4 pt-3 pb-6">
      {/* Top Header */}
      <div className="flex items-center gap-3 py-2 mb-3">
        <button
          onClick={() => navigate('/dashboard')}
          className="p-1 -ml-1 text-slate-600 hover:text-slate-900 transition-colors"
        >
          <ArrowLeft className="w-5 h-5" />
        </button>
        <h1 className="text-lg font-black text-slate-900 tracking-tight">
          {isCheckOut ? 'Check Out' : 'Check In'}
        </h1>
      </div>

      <div className="space-y-3.5 mb-6">
        {/* CARD 1: Location Captured (Screen 4 Mockup) */}
        <div className="bg-white rounded-2xl p-4 border border-slate-100 shadow-sm">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3.5">
              <div className="w-10 h-10 rounded-xl bg-blue-50 text-[#1D4ED8] flex items-center justify-center shrink-0">
                <MapPin className="w-5 h-5 stroke-[2.2px]" />
              </div>
              <div>
                <div className="font-extrabold text-slate-900 text-xs">Location Captured</div>
                {locationLoading ? (
                  <div className="text-[11px] text-slate-400 mt-0.5">Fetching GPS coordinates...</div>
                ) : coords ? (
                  <>
                    <div className="text-xs font-bold text-slate-700 mt-0.5">
                      {coords.lat}, {coords.lng}
                    </div>
                    <div className="text-[11px] text-slate-400">Accuracy: {coords.accuracy} m</div>
                  </>
                ) : null}
              </div>
            </div>

            {coords && (
              <div className="w-6 h-6 rounded-full bg-emerald-500 text-white flex items-center justify-center shrink-0 shadow-xs">
                <Check className="w-3.5 h-3.5 stroke-[3px]" />
              </div>
            )}
          </div>
        </div>

        {/* CARD 2: Photo Captured (Screen 4 Mockup) */}
        <div className="bg-white rounded-2xl p-4 border border-slate-100 shadow-sm">
          <div className="flex items-center justify-between mb-3">
            <div className="flex items-center gap-3.5">
              <div className="w-10 h-10 rounded-xl bg-blue-50 text-[#1D4ED8] flex items-center justify-center shrink-0">
                <Camera className="w-5 h-5 stroke-[2.2px]" />
              </div>
              <div className="font-extrabold text-slate-900 text-xs">Photo Captured</div>
            </div>

            {capturedImage && (
              <div className="w-6 h-6 rounded-full bg-emerald-500 text-white flex items-center justify-center shrink-0 shadow-xs">
                <Check className="w-3.5 h-3.5 stroke-[3px]" />
              </div>
            )}
          </div>

          {/* Camera Viewport / Captured Preview */}
          <div className="w-full h-56 rounded-xl overflow-hidden bg-slate-900 relative flex items-center justify-center shadow-inner">
            {capturedImage ? (
              <img
                src={capturedImage}
                alt="Captured Student Selfie"
                className="w-full h-full object-cover"
              />
            ) : cameraError ? (
              <div className="text-center p-4">
                <AlertCircle className="w-8 h-8 text-amber-400 mx-auto mb-2" />
                <p className="text-xs text-slate-300 mb-3">{cameraError}</p>
                <button
                  type="button"
                  onClick={handleCapturePhoto}
                  className="px-4 py-2 bg-blue-600 hover:bg-blue-500 text-white rounded-xl text-xs font-bold transition-all"
                >
                  Use Demo Camera Snapshot
                </button>
              </div>
            ) : (
              <video
                ref={videoRef}
                autoPlay
                playsInline
                muted
                className="w-full h-full object-cover"
              />
            )}
          </div>

          {/* Camera Buttons */}
          <div className="mt-3 flex gap-2">
            {!capturedImage ? (
              <button
                type="button"
                onClick={handleCapturePhoto}
                className="w-full py-2.5 bg-blue-50 hover:bg-blue-100 text-[#1D4ED8] rounded-xl font-bold text-xs flex items-center justify-center gap-2 transition-all cursor-pointer"
              >
                <Camera className="w-4 h-4" />
                <span>Take Snapshot</span>
              </button>
            ) : (
              <button
                type="button"
                onClick={handleRetakePhoto}
                className="w-full py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-xl font-semibold text-xs flex items-center justify-center gap-1.5 transition-all cursor-pointer"
              >
                <RefreshCw className="w-3.5 h-3.5" />
                <span>Retake Photo</span>
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Submit Button (Screen 4 Bottom) */}
      <button
        onClick={handleSubmit}
        disabled={!isFormComplete || submitting}
        className={`w-full h-12 rounded-xl text-white font-bold text-sm flex items-center justify-center gap-2 transition-all mt-auto shadow-md ${
          isCheckOut
            ? isFormComplete
              ? 'bg-gradient-to-r from-[#E11D48] to-[#BE123C] hover:from-rose-600 hover:to-rose-700 shadow-rose-500/25 active:scale-[0.98] cursor-pointer'
              : 'bg-slate-300 cursor-not-allowed shadow-none'
            : isFormComplete
            ? 'bg-gradient-to-r from-[#1D4ED8] to-[#2563EB] hover:from-blue-700 hover:to-blue-600 shadow-blue-500/25 active:scale-[0.98] cursor-pointer'
            : 'bg-slate-300 cursor-not-allowed shadow-none'
        }`}
      >
        <span>
          {submitting
            ? 'Submitting...'
            : isCheckOut
            ? 'Submit Check Out'
            : 'Submit Check In'}
        </span>
      </button>
    </div>
  );
};
