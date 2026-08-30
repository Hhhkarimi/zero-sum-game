# وب‌اپ نقطه صفر

نسخه وب بازی با Next.js و TypeScript. منطق بازی از موتور Android منتقل شده، اما متن رابط فقط از علامت‌های `+` و `−` و قواعد عددی استفاده می‌کند.

## قابلیت‌ها

- کنترل مستقیم با swipe یا drag، بدون دکمه جهت
- پشتیبانی از کلیدهای جهت‌دار و WASD
- سه حالت بی‌نهایت، روزانه و ۱۰ معمای حل‌پذیر
- برگشت تا ۸ حرکت، رکورد محلی، صدا و بازخورد لمسی امن
- رابط RTL واکنش‌گرا، کنتراست مناسب و پشتیبانی از کاهش حرکت
- هدرهای امنیتی، CSP و خروجی استاتیک مسیر اصلی

## اجرای محلی

```bash
npm ci
npm run dev
```

بررسی کامل:

```bash
npm run check
npm run build
npm audit --omit=dev --audit-level=high
```

## استقرار Vercel

هنگام Import کردن ریپو در Vercel:

- Root Directory: `web`
- Framework Preset: `Next.js`
- Install Command: `npm ci`
- Build Command: `npm run build`
- متغیر محیطی: ندارد

Vercel خروجی را از روی `package-lock.json` می‌سازد. هدرهای امنیتی در `next.config.ts` تعریف شده‌اند.
