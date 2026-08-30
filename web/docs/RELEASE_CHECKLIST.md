# دروازه انتشار وب‌اپ

این فایل از بخش‌های مرتبط ریپوی `pre-production-checklist` برای Next.js، Vercel، امنیت فرانت‌اند، دسترس‌پذیری و کارایی استخراج شده است. موارد مربوط به API، احراز هویت، دیتابیس و آپلود در این نسخه کاربرد ندارند، چون بازی کاملاً سمت کاربر اجرا می‌شود.

## کنترل‌های خودکار

- [x] TypeScript در حالت strict و بدون خطای typecheck
- [x] ESLint بدون warning
- [x] تست موتور، قواعد برخورد، gesture و حل‌پذیری تمام معماها
- [x] build تولید Next.js
- [x] audit وابستگی‌های production با سطح high
- [x] اجرای خودکار همین کنترل‌ها در GitHub Actions

دستور واحد:

```bash
npm run check && npm run build && npm audit --omit=dev --audit-level=high
```

## امنیت و حریم خصوصی

- [x] CSP با `object-src 'none'`، `frame-ancestors 'none'` و محدودیت منبع‌ها
- [x] هدرهای `nosniff`، Referrer Policy، Permissions Policy و جلوگیری از iframe
- [x] بدون secret، API key، فرم یا ورودی HTML پویا
- [x] دسترسی به localStorage، Web Audio، vibration، clipboard و Web Share داخل `try/catch`
- [x] داده محلی نسخه‌بندی شده و parser آن در برابر JSON خراب مقاوم است
- [x] هیچ SDK تبلیغاتی یا analytics در build وجود ندارد

## تجربه و دسترس‌پذیری

- [x] کنترل touch و pointer با `touch-action: none` فقط روی صفحه بازی
- [x] کنترل کامل صفحه‌کلید با Arrow و WASD
- [x] دکمه‌ها نام قابل خواندن، focus-visible و حداقل ارتفاع ۴۲ تا ۴۸ پیکسل دارند
- [x] صفحه قابلیت zoom مرورگر را محدود نمی‌کند
- [x] انیمیشن‌ها با `prefers-reduced-motion` خاموش می‌شوند
- [x] بازی در عرض ۳۲۰ پیکسل و چیدمان تک‌ستونه کار می‌کند
- [x] خطا یا نبودن sound، haptic، share و storage بازی را متوقف نمی‌کند

## کارایی

- [x] مسیر اصلی به‌صورت static prerender می‌شود
- [x] فونت و تصویر خارجی وجود ندارد
- [x] هر حرکت حداکثر ۱۶ کاشی را محاسبه و render می‌کند
- [x] state بازی در مرورگر است و درخواست شبکه در جریان بازی وجود ندارد
- [x] برای حرکت کم کاربر، transition و animation حذف می‌شود

## کنترل‌های Vercel پس از اولین استقرار

- [ ] Root Directory روی `web` تنظیم شده باشد
- [ ] Production Deployment از commit تأییدشده ساخته شده باشد
- [ ] HTTPS و redirect دامنه اصلی بررسی شود
- [ ] هدرهای امنیتی پاسخ production با `curl -I` بررسی شوند
- [ ] یک تست Lighthouse موبایل روی URL production ثبت شود
- [ ] rollback به deployment قبلی یک‌بار آزمایش شود

این شش مورد به URL واقعی Vercel وابسته‌اند و بعد از اولین استقرار بسته می‌شوند.
