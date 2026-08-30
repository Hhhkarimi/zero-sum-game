import type { Metadata } from "next";
import type { ReactNode } from "react";
import "./globals.css";

export const metadata: Metadata = {
  title: "نقطه صفر | بازی جمع و علامت",
  description:
    "یک بازی عددی چهار در چهار. کاشی‌های مثبت و منفی را ترکیب کنید و صفحه را به صفر برسانید.",
  applicationName: "نقطه صفر",
  appleWebApp: {
    capable: true,
    title: "نقطه صفر",
    statusBarStyle: "black-translucent",
  },
  formatDetection: {
    telephone: false,
  },
};

export const viewport = {
  width: "device-width",
  initialScale: 1,
  themeColor: "#171714",
};

export default function RootLayout({ children }: { children: ReactNode }) {
  return (
    <html lang="fa" dir="rtl">
      <body>{children}</body>
    </html>
  );
}
