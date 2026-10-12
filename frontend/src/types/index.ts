export type Role = 'student' | 'faculty' | 'admin';

export interface User {
  id: string;
  username: string;
  role: Role;
  fullName: string;
  collegeEmail: string;
  rtuRollNumber: string;
  branch: string;
  section: string;
  sessionToken?: string;
  deviceId?: string;
  hasAcceptedPermissions: boolean;
}

export type NoticeCategory = 'IMPORTANT' | 'GENERAL' | 'ACADEMIC';

export interface Notice {
  id: string;
  title: string;
  subTitle?: string;
  dateStr: string;
  category: NoticeCategory;
  message: string;
  isRead: boolean;
  dueDate?: string;
}

export type AttendanceStatus = 'PRESENT' | 'ABSENT' | 'OD' | 'NOT_MARKED';

export interface AttendanceRecord {
  id: string;
  dateStr: string; // e.g. "10 Oct"
  fullDate: string; // e.g. "2026-10-10"
  checkInTime: string | null; // e.g. "08:52 AM"
  checkOutTime: string | null; // e.g. "03:42 PM"
  status: AttendanceStatus;
}

export interface AttendanceReportSummary {
  presentCount: number;
  absentCount: number;
  odCount: number;
  totalClasses: number;
  overallPercentage: number;
  records: AttendanceRecord[];
}

export interface CalendarDay {
  dayNumber: number;
  dateStr: string;
  status: AttendanceStatus;
  isSunday: boolean;
  isToday: boolean;
}

export interface AttendanceWindowState {
  currentTimeStr: string;
  canCheckIn: boolean;
  canCheckOut: boolean;
  isBeforeWindow: boolean;
  isAfterWindow: boolean;
  isEveningCheckOut: boolean;
  buttonLabel: 'Check In' | 'Check Out' | 'Check-in Closed' | 'Checked Out' | 'Completed';
  buttonSubtext: string;
  buttonColor: 'blue' | 'red' | 'gray';
  hasCheckedInToday: boolean;
  hasCheckedOutToday: boolean;
  todayCheckInTime?: string;
  todayCheckOutTime?: string;
}
