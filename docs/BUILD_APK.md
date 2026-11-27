# Build APK

App memakai BE deploy `https://temukarya.bccdev.id/api/v1/` secara default, jadi HP cukup punya
internet (tidak perlu USB / adb reverse). Untuk BE lokal, isi `local.properties`:

```
api.base.url=http://localhost:8000/api/v1/
```

## 1. APK debug (paling cepat, untuk testing / dibagikan ke teman)

```
./gradlew assembleDebug
```

Hasil: `app/build/outputs/apk/debug/app-debug.apk`.
Android Studio: **Build → Generate App Bundles or APKs → Generate APKs**.

## 2. APK release (bertanda tangan)

Sekali saja, buat keystore (simpan baik-baik, jangan di-commit):

```
keytool -genkeypair -v -keystore temukarya-release.jks -alias temukarya \
  -keyalg RSA -keysize 2048 -validity 10000
```

Buat `keystore.properties` di root proyek:

```
storeFile=temukarya-release.jks
storePassword=PASSWORD_KEYSTORE
keyAlias=temukarya
keyPassword=PASSWORD_KEY
```

Lalu:

```
./gradlew assembleRelease
```

Hasil: `app/build/outputs/apk/release/app-release.apk`.
Tanpa `keystore.properties`, hasilnya `app-release-unsigned.apk` yang tidak bisa di-install.

Android Studio: **Build → Generate Signed App Bundle or APK → APK**, pilih keystore di atas, variant `release`.

## Install ke HP

- Kirim file `.apk` ke HP (WhatsApp/Drive/kabel), buka, izinkan "Install dari sumber tidak dikenal".
- Atau lewat USB: `adb install -r app/build/outputs/apk/debug/app-debug.apk`.
- APK debug dan release punya tanda tangan berbeda: uninstall versi lama dulu kalau ganti jenis.
