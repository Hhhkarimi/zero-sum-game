# Zero Sum Game

بازی عددی «نقطه صفر» در دو بخش نگه‌داری می‌شود:

- `web/`: وب‌اپ Next.js با رابط فارسی و کنترل swipe، ماوس و صفحه‌کلید
- `app/`: نسخه فعلی Android با Kotlin و Jetpack Compose

## اجرای وب‌اپ

```bash
cd web
npm ci
npm run dev
```

دروازه کامل قبل از انتشار:

```bash
cd web
npm run check
npm run build
```

## انتشار روی Vercel

ریپو را در Vercel وارد کنید و **Root Directory** را روی `web` بگذارید. Vercel فریم‌ورک Next.js و دستور build را خودکار تشخیص می‌دهد و این پروژه متغیر محیطی لازم ندارد.

جزئیات کنترل‌های انتشار در [`web/docs/RELEASE_CHECKLIST.md`](web/docs/RELEASE_CHECKLIST.md) آمده است.
